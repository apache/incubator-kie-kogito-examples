/*
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

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class AssignmentResourceTest {

    @Test
    void verifyAssignPatientToDoctor() {
        given()
                .body("{ \"id\": \"12345\", \"name\": \"Mick\", \"dateOfBirth\": \"1983-08-15\", \"symptoms\":[\"seizures\"]}")
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/onboarding/assignment")
                .then()
                .statusCode(200)
                .body("assignedDoctor.id", stringContainsInOrder("8293dc94-2386-11eb-adc1-0242ac120002"));
    }
}
