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
package org.kie.dmn.kogito.springboot.tracing;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.kafka.KafkaClient;
import org.kie.kogito.tracing.decision.event.CloudEventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.dmn.kogito.springboot.tracing.matcher.StringMatchesUUIDPattern.matchesThePatternOfAUUID;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
public class LoanEligibilityIT {

    public static final String KOGITO_EXECUTION_ID_HEADER = "X-Kogito-execution-id";
    public static final String TRACING_TOPIC_NAME = "kogito-tracing-decision";
    public static final String TRACING_MODELS_TOPIC_NAME = "kogito-tracing-model";

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanEligibilityIT.class);

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer();

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("kogito.addon.tracing.decision.kafka.bootstrapAddress", kafkaContainer::getBootstrapServers);
    }

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testEvaluateLoanEligibility() throws InterruptedException {
        final KafkaClient kafkaClient = new KafkaClient(kafkaContainer.getBootstrapServers());
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        try {
            kafkaClient.consume(TRACING_TOPIC_NAME, s -> {
                LOGGER.info("Received from kafka: {}", s);
                Optional.ofNullable(CloudEventUtils.decode(s))
                        .ifPresentOrElse(
                                cloudEvent -> countDownLatch.countDown(),
                                () -> LOGGER.error("Error parsing {}", s)
                        );
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
            assertEquals( 0, countDownLatch.getCount());
        } finally {
            kafkaClient.shutdown();
        }
    }

    @Test
    public void testEvaluateDMNModel() throws InterruptedException {
        final KafkaClient kafkaClient = new KafkaClient(kafkaContainer.getBootstrapServers());
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        try {
            kafkaClient.consume(TRACING_MODELS_TOPIC_NAME, s -> {
                LOGGER.info("Received from kafka: {}", s);
                Optional.ofNullable(CloudEventUtils.decode(s))
                        .ifPresentOrElse(
                                cloudEvent -> countDownLatch.countDown(),
                                () -> LOGGER.error("Error parsing {}", s)
                        );
            });

            countDownLatch.await(5, TimeUnit.SECONDS);
            assertEquals(0, countDownLatch.getCount());
        } finally {
            kafkaClient.shutdown();
        }
    }
}
