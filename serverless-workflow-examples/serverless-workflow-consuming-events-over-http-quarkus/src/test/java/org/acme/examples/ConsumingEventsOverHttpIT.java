/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.acme.examples;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@QuarkusIntegrationTest
class ConsumingEventsOverHttpIT {

    private static final String START_CHANNEL_NAME = "start";

    private static final String MOVE_CHANNEL_NAME = "move";

    @BeforeAll
    static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testEventOverHttp() {

        ExtractableResponse<Response> response = given()
                .contentType(ContentType.JSON)
                .when()
                .body(generateCloudEvent(START_CHANNEL_NAME, "kogitobusinesskey", "test-business-key",
                        Collections.singletonMap("message", "Hello!")))
                .post("/startevent")
                .then()
                .log()
                .all()
                .statusCode(202)
                .extract();


        String id = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/start")
                .then()
                .statusCode(200)
                .extract().path("[0].id");


        given()
                .contentType(ContentType.JSON)
                .when()
                .body(generateCloudEvent(MOVE_CHANNEL_NAME, "kogitoprocrefid", id, Collections.singletonMap(MOVE_CHANNEL_NAME,
                        "This has been injected by the event")))
                .post("/move")
                .then()
                .statusCode(202);

        await()
                .atLeast(1, SECONDS)
                .atMost(30, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get("/start/{id}", id)
                        .then()
                        .statusCode(404));
    }

    private String generateCloudEvent(String channelName, String extensionKey, String value, Map<String, String> data) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(JsonFormat.getCloudEventJacksonModule())
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        try {
            return objectMapper.writeValueAsString(CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create(""))
                    .withType(channelName)
                    .withTime(OffsetDateTime.now())
                    .withExtension(extensionKey, value)
                    .withData(objectMapper.writeValueAsBytes(data))
                    .build());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
