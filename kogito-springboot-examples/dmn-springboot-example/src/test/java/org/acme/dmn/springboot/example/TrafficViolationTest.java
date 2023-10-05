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
package org.acme.dmn.springboot.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TrafficViolationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testEvaluateTrafficViolation() {
        given()
                .body("{\n" +
                        "    \"Driver\": {\n" +
                        "        \"Points\": 2\n" +
                        "    },\n" +
                        "    \"Violation\": {\n" +
                        "        \"Type\": \"speed\",\n" +
                        "        \"Actual Speed\": 120,\n" +
                        "        \"Speed Limit\": 100\n" +
                        "    }\n" +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/Traffic Violation")
                .then()
                .statusCode(200)
                .body("'Should the driver be suspended?'", is("No"));
    }
}
