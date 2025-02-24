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

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
class CallbackRestIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testCallbackRest() {
         await()
            .atMost(2, MINUTES)
            .pollDelay(2, SECONDS)
            .pollInterval(1, SECONDS)
            .untilAsserted(() -> {
                given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .get("/q/health")
                    .then()
                    .statusCode(200);
            });
            String id = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post("/callback")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        await()
                .atLeast(1, SECONDS)
                .atMost(30, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get("/callback/{id}", id)
                        .then()
                        .statusCode(404));
    }
}
