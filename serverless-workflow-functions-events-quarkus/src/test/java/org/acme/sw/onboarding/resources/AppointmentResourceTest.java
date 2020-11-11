/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.acme.sw.onboarding.resources;

import java.util.Date;

import javax.ws.rs.core.MediaType;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.stringContainsInOrder;

@QuarkusTest
class AppointmentResourceTest {

    @Test
    void verifySchedulePatientAppointmentWithoutDoctor() {
        given()
                .body("{ \"patients\": [{ \"id\": \"12345\", \"name\": \"Mick\", \"dateOfBirth\": \"1983-08-15\", \"gender\": \"male\", \"symptoms\":[\"seizures\"]}] }")
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/onboarding/schedule/appointment")
                .then()
                .statusCode(400)
                .body("message", stringContainsInOrder("Doctor has not been assigned"));
    }

    @Test
    void verifySchedulePatientAppointment() {
        given()
                .body("{ \"patients\": [{ \"assignedDoctor\": {\"id\": \"54321\"} , \"id\": \"12345\", \"name\": \"Mick\", \"dateOfBirth\": \"1983-08-15\", \"gender\": \"male\", \"symptoms\":[\"seizures\"]}] }")
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/onboarding/schedule/appointment")
                .then()
                .statusCode(200)
                .body("patients[0].id", equalTo("12345"));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get("/onboarding/schedule/appointment/patient/12345")
                .then()
                .statusCode(200)
                .body("", hasSize(1))
                .body("[0].patientId", equalTo("12345"))
                .body("[0].doctorId", equalTo("54321"))
                .body("[0].date", greaterThanOrEqualTo(new Date().getTime()));
    }
}