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

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusIntegrationTest
class GreetRestIT {

    @Test
    void testEnglish() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\" : \"John\", \"language\":\"English\"}").when()
                .post("/jsongreet")
                .then()
                .statusCode(201)
                .body("workflowdata.greeting", containsString("Hello"));
    }

    @Test
    void testSpanish() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\" : \"Javierito\", \"language\":\"Spanish\"}").when()
                .post("/jsongreet")
                .then()
                .statusCode(201)
                .body("workflowdata.greeting", containsString("Saludos"));
    }

    @Test
    void testDefaultLanguage() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\" : \"John\"}").when()
                .post("/jsongreet")
                .then()
                .statusCode(201)
                .body("workflowdata.greeting", containsString("Hello"));
    }

    @Test
    void testUnsupportedLanguage() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\" : \"Jan\", \"language\":\"Czech\"}").when()
                .post("/jsongreet")
                .then()
                .statusCode(201)
                .body("workflowdata.greeting", containsString("Hello"));
    }
}
