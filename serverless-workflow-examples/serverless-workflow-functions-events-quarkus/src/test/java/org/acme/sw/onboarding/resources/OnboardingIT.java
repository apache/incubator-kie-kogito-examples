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
package org.acme.sw.onboarding.resources;

import java.time.Duration;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
class OnboardingIT {

    @Test
    void verifyOnboardingWorkflow() {
        Integer appointments = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get("/onboarding/schedule/appointment")
                .then()
                .statusCode(200)
                .extract().path("$.size()");

        given()
                .body("{ \"name\": \"Yoda\", \"dateOfBirth\": \"1963-08-15\", \"symptoms\":[\"seizures\"]}")
                .header("ce-specversion", "1.0")
                .header("ce-type", "new.patient.events")
                .header("ce-source", "/hospital/entry")
                .header("ce-id", UUID.randomUUID().toString())
                .contentType(ContentType.JSON)
                .when()
                .post("/")
                .then()
                .statusCode(202);

        await().atMost(Duration.ofMinutes(1)).untilAsserted(() -> given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get("/onboarding/schedule/appointment")
                .then()
                .statusCode(200)
                .body("$", hasSize(appointments + 1))
                .body(format("[%s].doctor.name", appointments), is("Maria Mind"))
                .body(format("[%s].patient.name", appointments), is("Yoda")));
    }
}
