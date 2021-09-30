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

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.awaitility.Awaitility;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;

import com.jayway.jsonpath.JsonPath;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class OutboxIT {

    private static final Duration INITIAL_TIMEOUT = Duration.ofSeconds(250);
    private static final Duration TIMEOUT = Duration.ofSeconds(25);
    private static final Duration INTERVAL = Duration.ofSeconds(5);

    private static final String PROCESS_EVENTS_TOPIC = "kogito-processinstances-events";
    private static final String USERTASK_EVENTS_TOPIC = "kogito-usertaskinstances-events";

    @ConfigProperty(name = "kogito.port")
    private int kogitoPort;

    @ConfigProperty(name = "debezium.port")
    private int debeziumPort;

    @ConfigProperty(name = "kafka.port")
    private int kafkaPort;

    private KafkaTestClient kafkaClient;

    @BeforeEach
    void setup() {
        kafkaClient = new KafkaTestClient("localhost:" + kafkaPort);
    }

    @AfterEach
    void close() {
        if (kafkaClient != null) {
            kafkaClient.shutdown();
        }
    }

    @Test
    public void testSendProcessEvents() {
        // Check Debezium (Kafka, MongoDB) readiness
        Awaitility.given().ignoreExceptions()
                .await().atMost(INITIAL_TIMEOUT)
                .with().pollInterval(INTERVAL)
                .until(() -> {
                    given()
                            .port(kogitoPort)
                            .when()
                            .get("/orders")
                            .then()
                            .statusCode(200);
                    return true;
                });

        // Check Kogito App readiness
        Awaitility.given().ignoreExceptions()
                .await().atMost(INITIAL_TIMEOUT)
                .with().pollInterval(INTERVAL)
                .until(() -> {
                    given()
                            .port(debeziumPort)
                            .pathParam("connector", "kogito-connector")
                            .pathParam("task", 0)
                            .when()
                            .get("/connectors/{connector}/tasks/{task}/status")
                            .then()
                            .statusCode(200)
                            .assertThat()
                            .body("state", equalTo("RUNNING"));
                    return true;
                });

        // Check Debezium no Kafka topic created
        Awaitility.given().ignoreExceptions()
                .await().atMost(TIMEOUT)
                .with().pollInterval(INTERVAL)
                .until(() -> {
                    given()
                            .port(debeziumPort)
                            .pathParam("connector", "kogito-connector")
                            .when()
                            .get("/connectors/{connector}/topics")
                            .then()
                            .statusCode(200)
                            .assertThat()
                            .body("kogito-connector.topics", hasSize(0));
                    return true;
                });

        // Call Kogito App to publish events
        Awaitility.given().ignoreExceptions()
                .await().atMost(TIMEOUT)
                .with().pollInterval(INTERVAL)
                .until(() -> {
                    given()
                            .port(kogitoPort)
                            .header("Content-Type", "application/json")
                            .body("{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"23570\", \"shipped\" : false}}")
                            .when()
                            .post("/orders")
                            .then()
                            .statusCode(201)
                            .assertThat()
                            .body("approver", equalTo("john"))
                            .body("order.orderNumber", equalTo("23570"))
                            .body("order.shipped", equalTo(false));
                    return true;
                });

        // Check Debezium Kafka topic created
        Awaitility.given().ignoreExceptions()
                .await().atMost(TIMEOUT)
                .with().pollInterval(INTERVAL)
                .until(() -> {
                    given()
                            .port(debeziumPort)
                            .pathParam("connector", "kogito-connector")
                            .when()
                            .get("/connectors/{connector}/topics")
                            .then()
                            .statusCode(200)
                            .assertThat()
                            .body("kogito-connector.topics", hasSize(2))
                            .body("kogito-connector.topics", hasItem(PROCESS_EVENTS_TOPIC))
                            .body("kogito-connector.topics", hasItem(USERTASK_EVENTS_TOPIC));
                    return true;
                });

        // Check Kafka messages

        // Check process events pushed
        AtomicInteger processEventCounter = new AtomicInteger(0);
        kafkaClient.consume(PROCESS_EVENTS_TOPIC, message -> {
            String orderNumber = JsonPath.read(message, "$.data.variables.order.orderNumber");
            boolean shipped = JsonPath.read(message, "$.data.variables.order.shipped");
            if ("23570".equals(orderNumber) && !shipped) {
                processEventCounter.incrementAndGet();
            }
        });
        Awaitility.given().ignoreExceptions()
                .await().atMost(TIMEOUT)
                .with().pollInterval(INTERVAL)
                .until(() -> processEventCounter.intValue() == 2);

        // Check usertask events pushed
        AtomicInteger usertaskEventCounter = new AtomicInteger(0);
        kafkaClient.consume(USERTASK_EVENTS_TOPIC, message -> {
            String orderNumber = JsonPath.read(message, "$.data.inputs.input1.orderNumber");
            boolean shipped = JsonPath.read(message, "$.data.inputs.input1.shipped");
            if ("23570".equals(orderNumber) && !shipped) {
                usertaskEventCounter.incrementAndGet();
            }
        });
        Awaitility.given().ignoreExceptions()
                .await().atMost(TIMEOUT)
                .with().pollInterval(INTERVAL)
                .until(() -> usertaskEventCounter.intValue() == 1);
    }
}
