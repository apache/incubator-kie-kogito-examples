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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusIntegrationTest
class AppointmentResourceIT {

    @Test
    void verifySchedulePatientAppointmentWithoutDoctor() {
        given()
                .body("{ \"id\": \"12345\", \"name\": \"Mick\", \"dateOfBirth\": \"1983-08-15\", \"symptoms\":[\"seizures\"]}")
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/onboarding/schedule/appointment")
                .then()
                .statusCode(400)
                .body("message", stringContainsInOrder("Doctor has not been assigned"));
    }

    @Test
    void verifySchedulePatientAppointment() throws ParseException {
        given()
                .body("{ \"assignedDoctor\": {\"id\": \"54321\"} , \"id\": \"12345\", \"name\": \"Mick\", \"dateOfBirth\": \"1983-08-15\", \"symptoms\":[\"seizures\"]}")
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/onboarding/schedule/appointment")
                .then()
                .statusCode(200)
                .body("id", equalTo("12345"));

        final ValidatableResponse response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get("/onboarding/schedule/appointment/patient/12345")
                .then()
                .statusCode(200)
                .body("", hasSize(1))
                .body("[0].patient.id", equalTo("12345"))
                .body("[0].doctor.id", equalTo("54321"));

        final String date = JsonPath.from(response.extract().asString()).get("[0].date");
        final DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        assertThat(dt.parse(date), greaterThanOrEqualTo(new Date()));
    }
}
