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
package org.kie.dmn.kogito.quarkus.tracing;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.kie.kogito.cloudevents.CloudEventUtils;
import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.dmn.kogito.quarkus.tracing.matcher.StringMatchesUUIDPattern.matchesThePatternOfAUUID;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class LoanEligibilityIT {

    public static final String KOGITO_EXECUTION_ID_HEADER = "X-Kogito-execution-id";
    public static final String TRACING_TOPIC_NAME = "kogito-tracing-decision";
    public static final String TRACING_MODELS_TOPIC_NAME = "kogito-tracing-model";

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanEligibilityIT.class);

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    @Test
    public void testEvaluateLoanEligibility() throws InterruptedException {
        final KafkaClient kafkaClient = new KafkaClient(kafkaBootstrapServers);
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        try {
            kafkaClient.consume(TRACING_TOPIC_NAME, s -> {
                LOGGER.info("Received from kafka: {}", s);
                Optional.ofNullable(CloudEventUtils.decode(s))
                        .ifPresentOrElse(
                                cloudEvent -> countDownLatch.countDown(),
                                () -> LOGGER.error("Error parsing {}", s));
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
        } finally {
            kafkaClient.shutdown();
        }
    }

    @Test
    public void testEvaluateDMNModel() throws InterruptedException {
        final KafkaClient kafkaClient = new KafkaClient(kafkaBootstrapServers);
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        try {
            kafkaClient.consume(TRACING_MODELS_TOPIC_NAME, s -> {
                LOGGER.info("Received from kafka: {}", s);
                Optional.ofNullable(CloudEventUtils.decode(s))
                        .ifPresentOrElse(
                                cloudEvent -> countDownLatch.countDown(),
                                () -> LOGGER.error("Error parsing {}", s));
            });

            countDownLatch.await(5, TimeUnit.SECONDS);
            assertEquals(0, countDownLatch.getCount());
        } finally {
            kafkaClient.shutdown();
        }
    }
}
