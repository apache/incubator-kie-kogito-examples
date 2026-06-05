/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.kie.kogito.traffic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.traffic.licensevalidated.Validated;
import org.kie.kogito.traffic.licensevalidation.Driver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoApplication.class)
class TrafficValidationIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @LocalServerPort
    int randomServerPort;

    @BeforeEach
    void setup() {
        RestAssured.port = randomServerPort;
    }

    @Test
    void testTrafficValidationOnSpringBoot() {
        Map<String, Object> request = new HashMap<>();
        request.put("driver", new Driver("12-345", "Arthur", "SP", "Campinas", 2, 30, new Date()));
        request.put("currentTime", new Date());
        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/validation")
                .then()
                .statusCode(200)
                .body("ValidLicense[0]", is(false));
    }

    @Test
    void testTrafficValidateOnSpringBoot() {
        Map<String, Object> request = new HashMap<>();
        request.put("validated", new Validated("no"));
        request.put("currentTime", new Date());
        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/validate")
                .then()
                .statusCode(200)
                .body("ValidLicense[0]", is(true));
    }
}
