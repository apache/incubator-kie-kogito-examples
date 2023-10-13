/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.acme.travel.tests.multimessaging.quarkus;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.acme.travel.Traveller;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusIntegrationTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class MultiMessagingIT {

    public static final String TOPIC_PRODUCER = "travellers";
    public static final String TOPIC_PROCESSED_CONSUMER = "processedtravellers";
    public static final String TOPIC_CANCEL_CONSUMER = "cancelledtravellers";

    private static Logger LOGGER = LoggerFactory.getLogger(MultiMessagingIT.class);

    private ObjectMapper objectMapper;

    public KafkaTestClient kafkaClient;

    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(JsonFormat.getCloudEventJacksonModule());
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
    }

    @Test
    public void testProcess() throws InterruptedException {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        //number of generated events to test
        final int count = 3;
        final CountDownLatch countDownLatch = new CountDownLatch(count);

        kafkaClient.consume(Set.of(TOPIC_PROCESSED_CONSUMER, TOPIC_CANCEL_CONSUMER), s -> {
            LOGGER.info("Received from kafka: {}", s);
            try {
                JsonNode event = objectMapper.readValue(s, JsonNode.class);
                Traveller traveller = objectMapper.readValue(event.get("data").toString(), Traveller.class);
                assertEquals(!traveller.getNationality().equals("American"), traveller.isProcessed());
                assertTrue(traveller.getFirstName().matches("Name[0-9]+"));
                assertTrue(traveller.getLastName().matches("LastName[0-9]+"));
                assertTrue(traveller.getEmail().matches("email[0-9]+"));
                countDownLatch.countDown();
            } catch (JsonProcessingException e) {
                LOGGER.error("Error parsing {}", s, e);
                fail(e);
            }
        });

        IntStream.range(0, count)
                .mapToObj(i -> new Traveller("Name" + i, "LastName" + i, "email" + i, getNationality(i)))
                .forEach(traveller -> kafkaClient.produce(generateCloudEvent(traveller), TOPIC_PRODUCER));

        countDownLatch.await(10, TimeUnit.SECONDS);
        assertEquals(0, countDownLatch.getCount());
    }

    private String getNationality(int i) {
        return i % 2 == 0 ? "American" : "Spanish";
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

    @AfterEach
    public void stop() {
        Optional.ofNullable(kafkaClient).ifPresent(KafkaTestClient::shutdown);
    }
}
