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
package org.kie.kogito.springboot.outbox;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.test.springboot.kafka.KafkaTestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.jayway.jsonpath.JsonPath;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class OutboxIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxIT.class);
    private static final Duration TIMEOUT = Duration.ofMinutes(1);
    private static final Duration INTERVAL = Duration.ofSeconds(1);

    private static final String PROCESS_EVENTS_TOPIC = "kogito-processinstances-events";
    private static final String USERTASK_EVENTS_TOPIC = "kogito-usertaskinstances-events";
    private static final String VARIABLE_EVENTS_TOPIC = "kogito-variables-events";
    private static final int KOGITO_PORT = 8080;
    private static final int KAFKA_PORT = 9092;
    private static final int DEBEZIUM_PORT = 8083;

    @Container
    private static DockerComposeContainer<?> COMPOSE;

    private int kogitoPort;
    private int debeziumPort;
    private int kafkaPort;

    private KafkaTestClient kafkaClient;

    static {
        Path path = Paths.get("../../docker-compose.yml");
        if (!path.toFile().exists()) {
            path = Paths.get("docker-compose.yml");
        }
        COMPOSE = new DockerComposeContainer<>(path.toFile());
        COMPOSE.withPull(false);
        COMPOSE.withServices("kafka", "mongodb", "connect", "sidecar", "kogito");
        COMPOSE.withExposedService("kogito", KOGITO_PORT);
        COMPOSE.withExposedService("kafka", KAFKA_PORT);
        COMPOSE.withExposedService("connect", DEBEZIUM_PORT);
        COMPOSE.withLogConsumer("kafka", logger());
        COMPOSE.withLogConsumer("connect", logger());
        COMPOSE.withLogConsumer("sidecar", logger());
        COMPOSE.withLogConsumer("kogito", logger());
        COMPOSE.waitingFor("kafka", Wait.forListeningPort());
        COMPOSE.waitingFor("sidecar", Wait.forListeningPort());
        COMPOSE.waitingFor("kogito", Wait.forListeningPort());
        COMPOSE.withLocalCompose(true);
        //See https://github.com/testcontainers/testcontainers-java/issues/4565
        COMPOSE.withOptions("--compatibility");
    }

    private static Consumer<OutputFrame> logger() {
        return new Slf4jLogConsumer(LOGGER);
    }

    @BeforeEach
    void setup() {
        kogitoPort = COMPOSE.getServicePort("kogito", KOGITO_PORT);
        debeziumPort = COMPOSE.getServicePort("connect", DEBEZIUM_PORT);
        kafkaPort = COMPOSE.getServicePort("kafka", KAFKA_PORT);
        kafkaClient = new KafkaTestClient("localhost:" + kafkaPort);
    }

    @AfterEach
    void close() {
        if (kafkaClient != null) {
            kafkaClient.shutdown();
        }
    }

    @Test
    public void testSendProcessEvents() throws InterruptedException {
        // Check Debezium (Kafka, MongoDB) readiness
        await().ignoreExceptions()
                .atMost(TIMEOUT)
                .with().pollInterval(INTERVAL)
                .untilAsserted(() -> given()
                        .port(kogitoPort)
                        .when()
                        .get("/orders")
                        .then()
                        .statusCode(200));

        // Check Kogito App readiness
        await().ignoreExceptions()
                .atMost(TIMEOUT)
                .with().pollInterval(INTERVAL)
                .untilAsserted(() -> given()
                        .port(debeziumPort)
                        .pathParam("connector", "kogito-connector")
                        .pathParam("task", 0)
                        .when()
                        .get("/connectors/{connector}/tasks/{task}/status")
                        .then()
                        .statusCode(200)
                        .body("state", equalTo("RUNNING")));

        // Check Debezium no Kafka topic created
        await().atMost(TIMEOUT)
                .with().pollInterval(INTERVAL)
                .untilAsserted(() -> given()
                        .port(debeziumPort)
                        .pathParam("connector", "kogito-connector")
                        .when()
                        .get("/connectors/{connector}/topics")
                        .then()
                        .statusCode(200)
                        .body("kogito-connector.topics", hasSize(0)));

        // Check Kafka messages
        CountDownLatch processEventCounter = new CountDownLatch(2);
        CountDownLatch userTaskEventCounter = new CountDownLatch(1);
        kafkaClient.consume(Set.of(PROCESS_EVENTS_TOPIC, USERTASK_EVENTS_TOPIC), message -> {
            String type = JsonPath.read(message, "$.type");
            if ("ProcessInstanceEvent".equals(type)) {
                String orderNumber = JsonPath.read(message, "$.data.variables.order.orderNumber");
                boolean shipped = JsonPath.read(message, "$.data.variables.order.shipped");
                if ("23570".equals(orderNumber) && !shipped) {
                    processEventCounter.countDown();
                }
            } else if ("UserTaskInstanceEvent".equals(type)) {
                String orderNumber = JsonPath.read(message, "$.data.inputs.input1.orderNumber");
                boolean shipped = JsonPath.read(message, "$.data.inputs.input1.shipped");
                if ("23570".equals(orderNumber) && !shipped) {
                    userTaskEventCounter.countDown();
                }
            }
        });

        // Call Kogito App to publish events
        given()
                .port(kogitoPort)
                .header("Content-Type", "application/json")
                .body("{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"23570\", \"shipped\" : false}}")
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .body("approver", equalTo("john"))
                .body("order.orderNumber", equalTo("23570"))
                .body("order.shipped", equalTo(false));

        // Check Debezium Kafka topic created
        await().atMost(TIMEOUT)
                .with().pollInterval(INTERVAL)
                .untilAsserted(() -> given()
                        .port(debeziumPort)
                        .pathParam("connector", "kogito-connector")
                        .when()
                        .get("/connectors/{connector}/topics")
                        .then()
                        .statusCode(200)
                        .body("kogito-connector.topics", hasSize(3))
                        .body("kogito-connector.topics", hasItems(PROCESS_EVENTS_TOPIC, USERTASK_EVENTS_TOPIC, VARIABLE_EVENTS_TOPIC)));

        // Check process events pushed
        assertTrue(processEventCounter.await(TIMEOUT.getSeconds(), TimeUnit.SECONDS));
        assertTrue(userTaskEventCounter.await(TIMEOUT.getSeconds(), TimeUnit.SECONDS));
    }
}
