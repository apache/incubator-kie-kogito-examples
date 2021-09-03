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
package org.kie.kogito.examples.demo;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.JsonPath;

import io.vertx.core.Vertx;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.kafka.client.consumer.KafkaConsumer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class OutboxIT {

    @Test
    public void testSendProcessEvents() {
        Duration initialTimeout = Duration.ofSeconds(250);
        Duration timeout = Duration.ofSeconds(25);
        Duration interval = Duration.ofSeconds(5);

        String processEventsTopic = "kogito-processinstances-events";
        String usertaskEventsTopic = "kogito-usertaskinstances-events";

        int kogitoPort = Integer.parseInt(System.getProperty("kogito.port"));
        int debeziumPort = Integer.parseInt(System.getProperty("debezium.port"));
        int kafkaPort = Integer.parseInt(System.getProperty("kafka.port"));

        // Check Debezium (Kafka, MongoDB) readiness
        Awaitility.given().ignoreExceptions()
                .await().atMost(initialTimeout)
                .with().pollInterval(interval)
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
                .await().atMost(initialTimeout)
                .with().pollInterval(interval)
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
                .await().atMost(timeout)
                .with().pollInterval(interval)
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
                .await().atMost(timeout)
                .with().pollInterval(interval)
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
                .await().atMost(timeout)
                .with().pollInterval(interval)
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
                            .body("kogito-connector.topics", hasItem(processEventsTopic))
                            .body("kogito-connector.topics", hasItem(usertaskEventsTopic));
                    return true;
                });

        // Check Kafka messages
        Vertx vertx = Vertx.vertx();
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:" + kafkaPort);
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id", "outbox_test");
        config.put("auto.offset.reset", "earliest");
        config.put("enable.auto.commit", "false");

        // Check Kafka topics created
        KafkaConsumer<String, String> topicsConsumer = KafkaConsumer.create(vertx, config);
        Set<String> topics = new ConcurrentHashSet<>();
        topics.add(processEventsTopic);
        topics.add(usertaskEventsTopic);
        topicsConsumer.listTopics()
                .onSuccess(partitionsTopicMap -> partitionsTopicMap.forEach((topic, partitions) -> topics.remove(topic)));
        Awaitility.given().ignoreExceptions()
                .await().atMost(timeout)
                .with().pollInterval(interval)
                .until(topics::isEmpty);

        // Check process events pushed
        KafkaConsumer<String, String> processEventConsumer = KafkaConsumer.create(vertx, config);
        AtomicInteger processEventCounter = new AtomicInteger(0);
        processEventConsumer.handler(record -> {
            String message = record.value();
            String orderNumber = JsonPath.read(message, "$.data.variables.order.orderNumber");
            boolean shipped = JsonPath.read(message, "$.data.variables.order.shipped");
            if ("23570".equals(orderNumber) && !shipped) {
                processEventCounter.incrementAndGet();
            }
        });
        processEventConsumer.subscribe(processEventsTopic);
        Awaitility.given().ignoreExceptions()
                .await().atMost(timeout)
                .with().pollInterval(interval)
                .until(() -> processEventCounter.intValue() == 2);

        // Check usertask events pushed
        KafkaConsumer<String, String> usertaskEventConsumer = KafkaConsumer.create(vertx, config);
        AtomicInteger usertaskEventCounter = new AtomicInteger(0);
        usertaskEventConsumer.handler(record -> {
            String message = record.value();
            String orderNumber = JsonPath.read(message, "$.data.inputs.input1.orderNumber");
            boolean shipped = JsonPath.read(message, "$.data.inputs.input1.shipped");
            if ("23570".equals(orderNumber) && !shipped) {
                usertaskEventCounter.incrementAndGet();
            }
        });
        usertaskEventConsumer.subscribe(usertaskEventsTopic);
        Awaitility.given().ignoreExceptions()
                .await().atMost(timeout)
                .with().pollInterval(interval)
                .until(() -> usertaskEventCounter.intValue() == 1);
    }
}
