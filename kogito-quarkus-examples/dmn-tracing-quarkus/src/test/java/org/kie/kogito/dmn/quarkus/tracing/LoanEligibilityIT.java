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
package org.kie.kogito.dmn.quarkus.tracing;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.kie.kogito.tracing.event.model.models.DecisionModelEvent;
import org.kie.kogito.tracing.event.trace.TraceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.kie.kogito.dmn.quarkus.tracing.matcher.StringMatchesUUIDPattern.matchesThePatternOfAUUID;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class LoanEligibilityIT {

    public static final String KOGITO_EXECUTION_ID_HEADER = "X-Kogito-execution-id";
    public static final String TRACING_TOPIC_NAME = "kogito-tracing-decision";
    public static final String TRACING_MODELS_TOPIC_NAME = "kogito-tracing-model";

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanEligibilityIT.class);

    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    private KafkaTestClient kafkaClient;

    @BeforeEach
    public void setup() {
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
    }

    @AfterEach
    public void close() {
        if (kafkaClient != null) {
            kafkaClient.shutdown();
        }
    }

    @Test
    public void testEvaluateLoanEligibility() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        kafkaClient.consume(TRACING_TOPIC_NAME, s -> {
            LOGGER.info("Received from kafka: {}", s);

            if (checkDeserialization(s, TraceEvent.class) && isTraceEventComplete(s)) {
                countDownLatch.countDown();
            } else {
                fail("Decision trace event is not valid");
            }
        });

        given()
                .body("{" +
                        "    \"Client\": {" +
                        "        \"age\": 43," +
                        "        \"salary\": 1950," +
                        "        \"existing payments\": 100" +
                        "    }," +
                        "    \"Loan\": {" +
                        "        \"duration\": 15," +
                        "        \"installment\": 180" +
                        "    }," +
                        "    \"SupremeDirector\" : \"Yes\"," +
                        "    \"Bribe\": 1000" +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/LoanEligibility")
                .then()
                .statusCode(200)
                .header(KOGITO_EXECUTION_ID_HEADER, matchesThePatternOfAUUID())
                .body("'Decide'", is(true));

        countDownLatch.await(5, TimeUnit.SECONDS);
        assertEquals(0, countDownLatch.getCount());
    }

    @Test
    public void testEvaluateDMNModel() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        kafkaClient.consume(TRACING_MODELS_TOPIC_NAME, s -> {
            LOGGER.info("Received from kafka: {}", s);
            if (checkDeserialization(s, DecisionModelEvent.class) && isModelEventComplete(s)) {
                countDownLatch.countDown();
            } else {
                fail("Model event is not valid");
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);
        assertEquals(0, countDownLatch.getCount());
    }

    private <T> boolean checkDeserialization(String s, Class<T> clazz) {
        try {
            CloudEventUtils.decodeData(CloudEventUtils.decode(s).get(), clazz).get();
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to deserialize the CloudEvent", e);
            return false;
        }
    }

    private boolean isModelEventComplete(String s) {
        return s.contains("definitions") && s.contains("DMNDiagram") && s.contains("dmn-tracing-quarkus")
                && s.contains("gav") && s.contains("MODEL");
    }

    private boolean isTraceEventComplete(String s) {
        return s.contains("existing payments") && s.contains("inputs") && s.contains("outputs")
                && s.contains("executionSteps") && s.contains("additionalData")
                && s.contains("\"baseType\":\"number\"");
    }
}
