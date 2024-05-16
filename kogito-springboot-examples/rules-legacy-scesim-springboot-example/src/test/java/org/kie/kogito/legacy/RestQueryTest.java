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
package org.kie.kogito.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RestQueryTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    private static final String JSON_APPROVED_PAYLOAD =
            "{\n" +
                    "  \"approved\":false,\n" +
                    "  \"greeting\":\"foo\"\n" +
                    "}";
    private static final String JSON_DENIED_PAYLOAD =
            "{\n" +
                    "  \"approved\":false,\n" +
                    "  \"greeting\":\"bar\"\n" +
                    "}";

    @Test
    public void testApproved() {
        given()
                .body(JSON_APPROVED_PAYLOAD)
                .contentType(ContentType.JSON)
                .when()
                .post("/find-approved")
                .then()
                .statusCode(200)
                .body("approved", is(true));
    }

    @Test
    public void testDenied() {
        given()
                .body(JSON_DENIED_PAYLOAD)
                .contentType(ContentType.JSON)
                .when()
                .post("/find-approved")
                .then()
                .statusCode(200)
                .body("approved", is(false));
    }
}
