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
package org.kie.dmn.kogito.quarkus.tracing;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.kie.kogito.tracing.decision.event.trace.TraceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class LoanEligibilityTest {

    private static Logger LOGGER = LoggerFactory.getLogger(LoanEligibilityTest.class);

    public static final String TOPIC_CONSUMER = "kogito-tracing-decision";

    @Inject
    private ObjectMapper objectMapper;

    public KafkaClient kafkaClient;

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    @Test
    public void testEvaluateLoanEligibility() throws InterruptedException {
        kafkaClient = new KafkaClient(kafkaBootstrapServers);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        kafkaClient.consume(TOPIC_CONSUMER, s -> {
            LOGGER.info("Received from kafka: {}", s);
            try {
                TraceEvent event = objectMapper.readValue(s, TraceEvent.class);
                assertNotNull(event);
                countDownLatch.countDown();
            } catch (JsonProcessingException e) {
                LOGGER.error("Error parsing {}", s, e);
                throw new RuntimeException(e);
            }
        });

        given()
                .body("{" +
                              "\"Client\": " +
                              "{\"age\": 43,\"salary\": 1950,\"existing payments\": 100}," +
                              "\"Loan\": {\"duration\": 15,\"installment\": 180}, " +
                              "\"SupremeDirector\" : \"Yes\", " +
                              "\"Bribe\": 1000" +
                              "}"
                )
                .contentType(ContentType.JSON)
                .when()
                .post("/LoanEligibility")
                .then()
                .statusCode(200)
                .body("'Decide'", is(true));

        countDownLatch.await(5, TimeUnit.SECONDS);
        assertEquals(countDownLatch.getCount(), 0);
    }
}