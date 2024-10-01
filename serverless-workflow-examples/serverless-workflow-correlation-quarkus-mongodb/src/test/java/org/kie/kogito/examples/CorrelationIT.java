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
package org.kie.kogito.examples;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusIntegrationTest
class CorrelationIT {

    public static final String HEALTH_URL = "/q/health";
    public static final int TIMEOUT = 2;

    private String userId = "12345";

    @Test
    void testCorrelation() {
        //health check - wait to be ready
        await()
                .atMost(TIMEOUT, MINUTES)
                .pollDelay(2, SECONDS)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get(HEALTH_URL)
                        .then()
                        .statusCode(200));

        //start workflow
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("userId", userId)
                .post("/account/{userId}")
                .then()
                .statusCode(201);

        //check instance created
        AtomicReference<String> processInstanceId = new AtomicReference<>();
        await().atMost(TIMEOUT, MINUTES)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> processInstanceId.set(given()
                        .accept(ContentType.JSON)
                        .pathParam("userId", userId)
                        .get("/account/{userId}")
                        .then()
                        .statusCode(200)
                        .body("processInstanceId", notNullValue())
                        .extract()
                        .body().path("processInstanceId")));

        //check instance completed
        await()
                .atMost(TIMEOUT, MINUTES)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .pathParam("processInstanceId", processInstanceId.get())
                        .get("/correlation/{processInstanceId}")
                        .then()
                        .statusCode(404));
    }
}
