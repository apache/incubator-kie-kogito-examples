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
package org.acme.numbers;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(NumbersMockService.class)
class RestExampleTestIT {

    @BeforeAll
    static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @QuarkusTestProperty(name = "wiremock.port")
    int port;

    @Test
    void testRestExample() {
        Map<String, Object> body = new HashMap<>();
        body.put("numbers", new int[] { 1, 2, 3, 4, 5, 6, 7 });
        body.put("port", getPort());
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post("/RestExample")
                .then()
                .statusCode(201);
    }

    private int getPort() {
        return port == 0 ? NumbersMockService.serverPort() : port;
    }
}
