/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.acme.travel;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class MessagingIT {

    public static final String TOPIC_PRODUCER = "travellers";
    public static final String TOPIC_CONSUMER = "processedtravellers";
    private static Logger LOGGER = LoggerFactory.getLogger(MessagingIT.class);

    @Inject
    private ObjectMapper objectMapper;

    public KafkaClient kafkaClient;

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    @Test
    public void testProcess() throws InterruptedException {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        kafkaClient = new KafkaClient(kafkaBootstrapServers);

        //number of generated events to test
        final int count = 3;
        final CountDownLatch countDownLatch = new CountDownLatch(count);

        kafkaClient.consume(TOPIC_CONSUMER, s -> {
            LOGGER.info("Received from kafka: {}", s);
            try {
                JsonNode event = objectMapper.readValue(s, JsonNode.class);
                Traveller traveller = objectMapper.readValue(event.get("data").toString(), Traveller.class);
                assertTrue(traveller.isProcessed());
                assertTrue(traveller.getFirstName().matches("Name[0-9]+"));
                assertTrue(traveller.getLastName().matches("LastName[0-9]+"));
                assertTrue(traveller.getEmail().matches("email[0-9]+"));
                assertTrue(traveller.getNationality().matches("Nationality[0-9]+"));
                countDownLatch.countDown();
            } catch (JsonProcessingException e) {
                LOGGER.error("Error parsing {}", s, e);
                fail(e);
            }
        });

        IntStream.range(0, count)
                .mapToObj(i -> new Traveller("Name" + i, "LastName" + i, "email" + i, "Nationality" + i))
                .forEach(traveller -> kafkaClient.produce(generateCloudEvent(traveller), TOPIC_PRODUCER));

        countDownLatch.await(10, TimeUnit.SECONDS);
        assertEquals(0, countDownLatch.getCount());
    }

    private String generateCloudEvent(Traveller traveller) {
        assertFalse(traveller.isProcessed());
        try {
            return objectMapper.writeValueAsString(CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create(""))
                    //Start message event name in handle-travellers.bpmn
                    .withType("travellers")
                    .withTime(OffsetDateTime.now())
                    .withData(objectMapper.writeValueAsString(traveller).getBytes())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void stop() {
        Optional.ofNullable(kafkaClient).ifPresent(KafkaClient::shutdown);
    }
}
