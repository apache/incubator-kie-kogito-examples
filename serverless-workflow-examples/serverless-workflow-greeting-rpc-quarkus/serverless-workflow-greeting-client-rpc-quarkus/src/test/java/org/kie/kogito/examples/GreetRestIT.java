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

import java.io.IOException;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.examples.sw.greeting.GreeterService;

import io.grpc.Server;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusIntegrationTest
class GreetRestIT {

    private static Server server;

    @BeforeAll
    static void setup() throws IOException {
        int port = ConfigProvider.getConfig().getValue("quarkus.grpc.clients.Greeter.port", Integer.class);
        server = GreeterService.buildServer(port);
        server.start();
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
                .body("{\"workflowdata\" : {\"name\" : \"John\", \"language\":\"English\"}}").when()
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
                .body("{\"workflowdata\" : {\"name\" : \"Javierito\", \"language\":\"Spanish\"}}").when()
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
                .body("{\"workflowdata\" : {\"name\" : \"John\"}}").when()
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
                .body("{\"workflowdata\" : {\"name\" : \"Jan\", \"language\":\"Czech\"}}").when()
                .post("/jsongreet")
                .then()
                .statusCode(201)
                .body("workflowdata.message", containsString("Hello"));
    }
}
