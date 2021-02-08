/*
 *  Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.examples;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.cloudevents.extension.KogitoExtension;
import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class DmnEventDrivenIT {

    public static final String REQUESTS_TOPIC_NAME = "dmn-event-driven-requests";
    public static final String RESPONSES_TOPIC_NAME = "dmn-event-driven-responses";

    private static final Logger LOG = LoggerFactory.getLogger(DmnEventDrivenIT.class);
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private static final String DEFAULT_EVENT_ID = "d54ace84-6788-46b6-a359-b308f8b21778";

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    @BeforeAll
    static void registerExtension() {
        KogitoExtension.register();
    }

    @Test
    public void test() {
        for (String evaluationType : List.of("evaluate_all", "evaluate_decision_service")) {
            for (String resultType : List.of("context_only", "full_result")) {
                for (String filterStatus : List.of("all", "filtered")) {
                    String basePath = String.join("/", "events", evaluationType, resultType, filterStatus);
                    doTest(basePath);
                }
            }
        }

        for (String errorSubPath : List.of("bad_request/null_data", "bad_request/null_model", "model_not_found")) {
            String basePath = "events/error/" + errorSubPath;
            doTest(basePath);
        }
    }

    private void assertJsonEquals(String expectedJson, String actualJson) throws Exception {
        String normalizedExpectedJson = normalizeJson(expectedJson);
        String normalizedActualJson = normalizeJson(actualJson);

        LOG.info("Normalized expected: " + normalizedExpectedJson);
        LOG.info("Normalized actual..: " + normalizedActualJson);

        JSONAssert.assertEquals(normalizedExpectedJson, normalizedActualJson, JSONCompareMode.NON_EXTENSIBLE);
    }

    private void doTest(String basePath) {
        LOG.info("Processing \"{}\"...", basePath);

        String inputJson = readResource(basePath + "/input.json");
        String outputJson = readResource(basePath + "/output.json");

        final KafkaClient kafkaClient = new KafkaClient(kafkaBootstrapServers);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference<String> outputEventRef = new AtomicReference<>();

        try {
            kafkaClient.consume(RESPONSES_TOPIC_NAME, eventString -> {
                LOG.info("Received from kafka: {}", eventString);
                Optional.ofNullable(eventString).filter(s -> !s.isBlank()).ifPresentOrElse(
                        e -> {
                            outputEventRef.set(e);
                            countDownLatch.countDown();
                        },
                        () -> LOG.error("Error parsing {}", eventString)
                );
            });

            await()
                    .atLeast(3, SECONDS)
                    .atMost(15, SECONDS)
                    .with().pollInterval(3, SECONDS)
                    .untilAsserted(() -> {
                        kafkaClient.produce(inputJson, REQUESTS_TOPIC_NAME);

                        assertTrue(countDownLatch.await(5, SECONDS));
                        assertJsonEquals(outputJson, outputEventRef.get());
                    });
        } finally {
            kafkaClient.shutdown();
        }
    }

    private static String normalizeJson(String jsonString) throws JsonProcessingException {
        JsonNode jsonNode = MAPPER.reader().readTree(jsonString);

        Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> child = it.next();
            if (child.getKey().equals("id")) {
                child.setValue(MAPPER.reader().readTree("\"" + DEFAULT_EVENT_ID + "\""));
            }
        }

        pruneNullNodes(jsonNode);
        return MAPPER.writer().writeValueAsString(jsonNode);
    }

    private static void pruneNullNodes(JsonNode node) {
        Iterator<JsonNode> it = node.iterator();
        while (it.hasNext()) {
            JsonNode child = it.next();
            if (child.isNull()) {
                it.remove();
            } else {
                pruneNullNodes(child);
            }
        }
    }

    private String readResource(String path) {
        return Optional.ofNullable(Thread.currentThread().getContextClassLoader().getResource(path))
                .map(URL::getPath)
                .map(Path::of)
                .map(p -> {
                    try {
                        return Files.readString(p);
                    } catch (IOException e) {
                        LOG.error("Error while reading resource " + path, e);
                        return null;
                    }
                })
                .orElseThrow(() -> new IllegalStateException("Can't read resource " + path));
    }
}
