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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ExampleResourceTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testCheckListSize() {
        given()
                .body("")
                .contentType(ContentType.JSON)
                .when()
                .get("/example/gauge/1")
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/actuator/prometheus")
                .then()
                .statusCode(200)
                .body(containsString("example_prime_number_total"));
    }

    @Test
    public void testCheckIfPrime() {
        given()
                .body("")
                .contentType(ContentType.JSON)
                .when()
                .get("/example/prime/13")
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/actuator/prometheus")
                .then()
                .statusCode(200)
                .body(containsString("example_prime_number_tested_total"));
    }

    @Test
    public void testCheckIfTaggedPrime() {
        given()
                .body("")
                .contentType(ContentType.JSON)
                .when()
                .get("/example/taggedprime/13")
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/actuator/prometheus")
                .then()
                .statusCode(200)
                .body(containsString("example_prime_number_total{type=\"prime\",}"));
    }
}
