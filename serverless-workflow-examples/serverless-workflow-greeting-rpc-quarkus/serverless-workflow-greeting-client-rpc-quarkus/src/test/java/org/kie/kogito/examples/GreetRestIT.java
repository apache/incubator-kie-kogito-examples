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

import java.io.IOException;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.examples.sw.greeting.GreeterService;

import io.grpc.Server;
import io.restassured.RestAssured;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;

@QuarkusIntegrationTest
class GreetRestIT {

    private static Server server;

    @BeforeAll
    static void setup() throws IOException {
        int port = ConfigProvider.getConfig().getValue("quarkus.grpc.clients.Greeter.port", Integer.class);
        server = GreeterService.buildServer(port);
        server.start();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterAll
    static void tearDown() throws InterruptedException {
        server.shutdownNow();
        server.awaitTermination();
        server = null;
    }

    @Test
    void testEnglish() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\" : \"John\", \"language\":\"English\"}").when()
                .post("/jsongreet")
                .then()
                .statusCode(201)
                .body("workflowdata.message", containsString("Hello"));
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
                .body("workflowdata.message", containsString("Saludos"));
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
                .body("workflowdata.message", containsString("Hello"));
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
                .body("workflowdata.message", containsString("Hello"));
    }

    @Test
    void testServerStreamAllLanguages() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\" : \"John\"}").when()
                .post("/jsongreetserverstream")
                .then()
                .statusCode(201)
                .body("workflowdata.response", hasSize(2))
                .body("workflowdata.response[0]", allOf(aMapWithSize(1), hasEntry("message", "Hello from gRPC service John")))
                .body("workflowdata.response[1]", allOf(aMapWithSize(1), hasEntry("message", "Saludos desde gRPC service John")));
    }

    @Test
    void testClientStreamGetMultipleLanguagesAtOnce() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"helloRequests\" : [" +
                        "{\"name\" : \"Javierito\", \"language\":\"Spanish\"}," +
                        "{\"name\" : \"John\", \"language\":\"English\"}," +
                        "{\"name\" : \"Jan\", \"language\":\"Czech\"}" +
                        "]}")
                .when()
                .post("/jsongreetclientstream")
                .then()
                .statusCode(201)
                .body("workflowdata.message", equalTo("Saludos desde gRPC service Javierito\n" +
                        "Hello from gRPC service John\n" +
                        "Hello from gRPC service Jan"));
    }

    @Test
    void testBiDiStreamGetMultipleLanguages() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"helloRequests\" : [" +
                        "{\"name\" : \"Javierito\", \"language\":\"Spanish\"}," +
                        "{\"name\" : \"John\", \"language\":\"English\"}," +
                        "{\"name\" : \"Jan\", \"language\":\"Czech\"}" +
                        "]}")
                .when()
                .post("/jsongreetbidistream")
                .then()
                .statusCode(201)
                .body("workflowdata.response", hasSize(3))
                .body("workflowdata.response[0]", allOf(aMapWithSize(1), hasEntry("message", "Saludos desde gRPC service Javierito")))
                .body("workflowdata.response[1]", allOf(aMapWithSize(1), hasEntry("message", "Hello from gRPC service John")))
                .body("workflowdata.response[2]", allOf(aMapWithSize(1), hasEntry("message", "Hello from gRPC service Jan")));
    }

    @Test
    void testBiDiStreamGetMultipleLanguagesError() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"helloRequests\" : [" +
                        "{\"name\" : \"Javierito\", \"language\":\"Spanish\"}," +
                        "{\"name\" : \"John\", \"language\":\"English\"}," +
                        "{\"name\" : \"Jan\", \"language\":\"Czech\"}" +
                        "]}")
                .when()
                .post("/jsongreetbidistreamerror")
                .then()
                .statusCode(500)
                .body("message", allOf(equalTo("OUT_OF_RANGE")));
    }

}
