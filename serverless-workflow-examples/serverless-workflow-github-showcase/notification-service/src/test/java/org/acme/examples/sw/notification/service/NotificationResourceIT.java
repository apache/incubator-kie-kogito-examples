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
package org.acme.examples.sw.notification.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

// Those are simple tests to verify if the integration is running.
// Edit the application.properties file with the right credentials, disabled this test and run.
// Check yr slack channel
@Disabled
@QuarkusTest
class NotificationResourceIT {

    @Test
    void simpleCheckSendSlackMessage() {
        given().when()
                .body("Hello World from silly integration test")
                .post("/plain")
                .then()
                .statusCode(200);
    }

    @Test
    void simpleCheckSendSlackMessageCloudEvent() {
        given().config(RestAssured.config().encoderConfig(RestAssured.config().getEncoderConfig().encodeContentTypeAs("application/cloudevents", ContentType.TEXT)))
                .when()
                .body("{ \"number\": 1000, \"pull_request\": { \"title\": \"Hello from cloud events! :cloud:\" } }")
                .header("ce-specversion", "1.0")
                .header("ce-id", "000")
                .header("ce-type", "notification")
                .header("ce-source", "http://github.com")
                .contentType("application/cloudevents")
                .post("/")
                .then()
                .statusCode(200);
    }
}
