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
package org.kie.kogito.dmn.quarkus.example.listener;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TrafficViolationTest {

    public static final String TRAFFIC_VIOLATION_TEST_BODY = "" +
            "{\n" +
            "    \"Driver\": {\n" +
            "        \"Points\": 2\n" +
            "    },\n" +
            "    \"Violation\": {\n" +
            "        \"Type\": \"speed\",\n" +
            "        \"Actual Speed\": 120,\n" +
            "        \"Speed Limit\": 100\n" +
            "    }\n" +
            "}";

    @Test
    public void testEvaluateTrafficViolation() {
        given()
                .body(TRAFFIC_VIOLATION_TEST_BODY)
                .contentType(ContentType.JSON)
                .when()
                .post("/Traffic Violation")
                .then()
                .statusCode(200)
                .body("'Should the driver be suspended?'", is("No"));
    }
}
