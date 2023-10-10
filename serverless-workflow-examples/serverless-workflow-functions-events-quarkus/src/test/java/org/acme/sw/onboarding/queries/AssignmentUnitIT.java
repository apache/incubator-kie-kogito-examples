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
package org.acme.sw.onboarding.queries;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@QuarkusIntegrationTest
class AssignmentUnitIT {

    @Test
    void verifyNeurologyAssignmentCollection() {
        given()
                .body("{ \"patients\": [{ \"name\": \"Mick\", \"dateOfBirth\": \"1983-08-15\", \"symptoms\":[\"seizures\"]}] }")
                .contentType(ContentType.JSON)
                .when()
                .post("/assign-doctor")
                .then()
                .statusCode(200)
                .body("assignedDoctor.specialty", hasItem("Neurology"));
    }

    @Test
    void verifyNeurologyAssignment() {
        given()
                .body("{ \"patients\": [{ \"name\": \"Mick\", \"dateOfBirth\": \"1983-08-15\", \"symptoms\":[\"seizures\"]}] }")
                .contentType(ContentType.JSON)
                .when()
                .post("/assign-doctor/first")
                .then()
                .statusCode(200)
                .body("assignedDoctor.specialty", equalTo("Neurology"));
    }
}
