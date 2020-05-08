/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.acme.travel;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.v03.CloudEventBuilder;
import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.tests.KogitoKafkaQuickstartSpringbootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(classes = KogitoKafkaQuickstartSpringbootApplication.class)
public class MessagingIntegrationTest {

    public static final String TOPIC_PRODUCER = "travellers";
    public static final String TOPIC_CONSUMER = "processedtravellers";
    private static Logger LOGGER = LoggerFactory.getLogger(MessagingIntegrationTest.class);

    @Autowired
    private ObjectMapper objectMapper;

    public KafkaTester kafkaTester;

    @Container
    private static final KafkaContainer KAFKA = new KafkaContainer()
            .withNetwork(Network.newNetwork())
            .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    @BeforeAll
    public static void init() {
        System.setProperty("kafka.bootstrap.servers", KAFKA.getBootstrapServers());
    }

    @Test
    public void testProcess() throws InterruptedException {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        kafkaTester = new KafkaTester(KAFKA.getBootstrapServers());

        //number of generated events to test
        final int count = 3;
        final CountDownLatch countDownLatch = new CountDownLatch(count);

        kafkaTester.consume(TOPIC_CONSUMER, s -> {
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
                throw new RuntimeException(e);
            }
        });

        IntStream.range(0, count)
                .mapToObj(i -> new Traveller("Name" + i, "LastName" + i, "email" + i, "Nationality" + i))
                .forEach(traveller -> kafkaTester.produce(generateCloudEvent(traveller), TOPIC_PRODUCER));

        countDownLatch.await(5, TimeUnit.SECONDS);
        assertEquals(countDownLatch.getCount(), 0);
    }

    private String generateCloudEvent(Traveller traveller) {
        assertFalse(traveller.isProcessed());
        try {
            return objectMapper.writeValueAsString(CloudEventBuilder.builder()
                                                           .withId(UUID.randomUUID().toString())
                                                           .withSource(URI.create(""))
                                                           .withType("TravelersMessageDataEvent_3")
                                                           .withTime(ZonedDateTime.now())
                                                           .withData(traveller)
                                                           .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void stop() {
        Optional.ofNullable(kafkaTester).ifPresent(KafkaTester::shutdown);
    }
}