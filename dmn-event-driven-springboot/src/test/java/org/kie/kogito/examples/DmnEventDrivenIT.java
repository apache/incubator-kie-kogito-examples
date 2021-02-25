/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import org.junit.jupiter.api.Test;
import org.kie.dmn.kogito.springboot.example.KogitoSpringbootApplication;
import org.kie.kogito.kafka.KafkaClient;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
public class DmnEventDrivenIT {

    public static final String REQUESTS_TOPIC_NAME = "dmn-event-driven-requests";
    public static final String RESPONSES_TOPIC_NAME = "dmn-event-driven-responses";

    private static final Logger LOG = LoggerFactory.getLogger(DmnEventDrivenIT.class);
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private static final String DEFAULT_EVENT_ID = "d54ace84-6788-46b6-a359-b308f8b21778";

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer();

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("kogito.addon.tracing.decision.kafka.bootstrapAddress", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
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

    private void assertCloudEventJsonEquals(String expectedJson, String actualJson) throws Exception {
        String normalizedExpectedJson = prepareCloudEventJsonForJSONAssert(expectedJson);
        String normalizedActualJson = prepareCloudEventJsonForJSONAssert(actualJson);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Normalized expected: " + normalizedExpectedJson);
            LOG.debug("Normalized actual..: " + normalizedActualJson);
        }

        JSONAssert.assertEquals(normalizedExpectedJson, normalizedActualJson, JSONCompareMode.NON_EXTENSIBLE);
    }

    private void doTest(String basePath) {
        LOG.debug("Processing \"{}\"...", basePath);

        String inputJson = readResource(basePath + "/input.json");
        String outputJson = readResource(basePath + "/output.json");

        final KafkaClient kafkaClient = new KafkaClient(kafkaContainer.getBootstrapServers());
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference<String> outputEventRef = new AtomicReference<>();

        try {
            kafkaClient.consume(RESPONSES_TOPIC_NAME, eventString -> {
                LOG.debug("Received from kafka: {}", eventString);
                Optional.ofNullable(eventString).filter(s -> !s.isBlank()).ifPresentOrElse(
                        e -> {
                            outputEventRef.set(e);
                            countDownLatch.countDown();
                        },
                        () -> LOG.error("Error parsing {}", eventString));
            });

            await()
                    .atLeast(3, SECONDS)
                    .atMost(15, SECONDS)
                    .with().pollInterval(3, SECONDS)
                    .untilAsserted(() -> {
                        kafkaClient.produce(inputJson, REQUESTS_TOPIC_NAME);

                        assertTrue(countDownLatch.await(5, SECONDS));
                        assertCloudEventJsonEquals(outputJson, outputEventRef.get());
                    });
        } finally {
            kafkaClient.shutdown();
        }
    }

    /**
     * This method receives a CloudEvent Json representation as input and returns a modified version that is suitable
     * to be evaluated with {@link JSONAssert#assertEquals(String, String, JSONCompareMode)} the way we need.
     *
     * The first change is set the value of the "id" field to a default one, only if present, to prevent the assertion
     * to fail since the actual id is randomly generated and will never be the same as the hardcoded expected one.
     *
     * The second change is the prune of "null" nodes from the tree, since the assertion would fail if in the expected
     * event a specific field is "null" and in the actual one is missing (or viceversa), but for us it's perfectly fine.
     *
     * @param jsonString input CloudEvent Json representation
     * @return modified CloudEvent Json representation
     * @throws JsonProcessingException if the input is not a valid Json string
     */
    private static String prepareCloudEventJsonForJSONAssert(String jsonString) throws JsonProcessingException {
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
