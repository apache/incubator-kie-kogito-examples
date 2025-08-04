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

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusIntegrationTest
class CallEchoIT {

    private static final String ACCESS_CIRCUIT_BREAKER_ECHO_ERROR = "HTTP 500 Internal Server Error";
    private static final String ACCESS_OPEN_CIRCUIT_ERROR = "org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException";

    @Test
    void circuitBreakerEcho() {
        // regular call, no failure.
        prepareCircuitBreakerCall().statusCode(201);

        // program the circuitBreakerEcho operation to fail.
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"operation\" : \"circuitBreakerEcho\", \"enabled\" : false}").when()
                .post("/external-service/admin")
                .then()
                .statusCode(200);

        // next 3 calls must fail with "HTTP 500 Internal Server Error"
        assertCallEcoStartsWithMessage(ACCESS_CIRCUIT_BREAKER_ECHO_ERROR);
        assertCallEcoStartsWithMessage(ACCESS_CIRCUIT_BREAKER_ECHO_ERROR);
        assertCallEcoStartsWithMessage(ACCESS_CIRCUIT_BREAKER_ECHO_ERROR);

        // fourth call must fail due to the Open circuit.
        assertCallEcoStartsWithMessage(ACCESS_OPEN_CIRCUIT_ERROR);
    }

    private static void assertCallEcoStartsWithMessage(String prefix) {
        JsonPath result = prepareCircuitBreakerCall()
                .statusCode(500)
                .extract()
                .jsonPath();
        String message = result.get("message");
        assertTrue(message != null && message.startsWith(prefix));
    }

    private static ValidatableResponse prepareCircuitBreakerCall() {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"echo\" : \"Hello!\"}").when()
                .post("/call-echo")
                .then();
    }
}
