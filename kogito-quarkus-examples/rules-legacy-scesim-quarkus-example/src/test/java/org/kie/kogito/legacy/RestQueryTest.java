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

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class RestQueryTest {

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
        Response response = given()
                .body(JSON_APPROVED_PAYLOAD)
                .contentType(ContentType.JSON)
                .when()
                .post("/find-approved");

        ResponseBody body = response.getBody();

        // To check for sub string presence get the Response body as a String.
        // Do a String.contains
        String bodyAsString = body.asString();
        System.out.println(bodyAsString);

        ValidatableResponse validatableResponse = response
                .then()
                .statusCode(200);
        validatableResponse
                .body("approved", is(true));
    }

    @Test
    public void testDenied() {
        Response response = given()
                .body(JSON_DENIED_PAYLOAD)
                .contentType(ContentType.JSON)
                .when()
                .post("/find-approved");

        ResponseBody body = response.getBody();

        // To check for sub string presence get the Response body as a String.
        // Do a String.contains
        String bodyAsString = body.asString();
        System.out.println(bodyAsString);

        ValidatableResponse validatableResponse = response
                .then()
                .statusCode(200);
        validatableResponse
                .body("approved", is(false));
    }
}
