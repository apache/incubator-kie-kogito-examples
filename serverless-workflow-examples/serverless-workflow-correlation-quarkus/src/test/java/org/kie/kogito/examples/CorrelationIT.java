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

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.notNullValue;
import static org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource.KOGITO_KAFKA_TOPICS;

@QuarkusIntegrationTest
@QuarkusTestResource(value = KafkaQuarkusTestResource.class,
        initArgs = { @ResourceArg(name = KOGITO_KAFKA_TOPICS, value = "validatedAccountEmail,validateAccountEmail,activateAccount,activatedAccount,newAccountEventType ") })
@QuarkusTestResource(PostgreSqlQuarkusTestResource.class)
class CorrelationIT {

    public static final String HEALTH_URL = "/q/health/ready";
    public static final int TIMEOUT = 2;

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private String userId = "12345";

    @Test
    void testCallbackRest() {
        //health check - wait to be ready
        await()
                .atMost(TIMEOUT, MINUTES)
                .with().pollInterval(1, SECONDS)
                .with()
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
        await()
                .atMost(TIMEOUT, MINUTES)
                .with().pollInterval(1, SECONDS)
                .with()
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
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .pathParam("processInstanceId", processInstanceId.get())
                        .get("/correlation/{processInstanceId}")
                        .then()
                        .statusCode(404));
    }
}
