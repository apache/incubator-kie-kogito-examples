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
package org.kie.kogito.dmn.springboot.tracing;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.test.springboot.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.springboot.KafkaSpringBootTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.kogito.dmn.springboot.tracing.matcher.StringMatchesUUIDPattern.matchesThePatternOfAUUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers = KafkaSpringBootTestResource.class)
public class LoanEligibilityIT {

    public static final String KOGITO_EXECUTION_ID_HEADER = "X-Kogito-execution-id";
    public static final String TRACING_TOPIC_NAME = "kogito-tracing-decision";
    public static final String TRACING_MODELS_TOPIC_NAME = "kogito-tracing-model";

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanEligibilityIT.class);

    @LocalServerPort
    private int port;

    @Autowired
    private KafkaTestClient kafkaClient;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testEvaluateLoanEligibility() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

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
    }

    @Test
    public void testEvaluateDMNModel() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        kafkaClient.consume(TRACING_MODELS_TOPIC_NAME, s -> {
            LOGGER.info("Received from kafka: {}", s);
            Optional.ofNullable(CloudEventUtils.decode(s))
                    .ifPresentOrElse(
                            cloudEvent -> countDownLatch.countDown(),
                            () -> LOGGER.error("Error parsing {}", s));
        });

        countDownLatch.await(5, TimeUnit.SECONDS);
        assertEquals(0, countDownLatch.getCount());
    }
}
