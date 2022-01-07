/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.hr;

import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
@QuarkusTestResource(PostgreSqlQuarkusTestResource.class)
public class HiringProcessIT {

    @Test
    public void testHiringProcess() {
        // test adding new hiring
        String addHiringPayload =
                "{ \"candidate\" : { \"name\" : \"John\", \"email\" : \"jon.doe@example.com\", \"salary\" : \"30000\", \"skills\" : \"kogito, java\" }}";

        // test getting the created hiring
        String hiringId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addHiringPayload)
                .when().post("/hiring")
                .then().log().ifValidationFails().statusCode(201).body("id", notNullValue()).extract().path("id");

        // get hiring  for review
        given().contentType(ContentType.JSON)
                .when()
                .get("/hiring")
                .then()
                .body("$.size()", is(1))
                .body("$.size()", is(1), "[0].id", is(hiringId));

        // get task for jdoe
        String hrReviewTaskId = given().accept(ContentType.JSON)
                .when().get("/hiring/{uuid}/tasks?user=jdoe", hiringId)
                .then().log().ifValidationFails().statusCode(200).body("$.size", is(1)).extract().path("[0].id");

        // complete HR interview task
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body("{\"approve\" : \"true\"}")
                .when().post("/hiring/{uuid}/HRInterview/{tuuid}?user=jdoe", hiringId, hrReviewTaskId)
                .then().log().ifValidationFails().statusCode(200);

        // get task for jdoe
        String itReviewTaskId = given().accept(ContentType.JSON)
                .when().get("/hiring/{uuid}/tasks?user=jdoe", hiringId)
                .then().log().ifValidationFails().statusCode(200).body("$.size", is(1)).extract().path("[0].id");

        // complete IT interview task
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body("{\"approve\" : \"true\"}")
                .when().post("/hiring/{uuid}/ITInterview/{tuuid}?user=jdoe", hiringId, itReviewTaskId)
                .then().log().ifValidationFails().statusCode(200);

        //verify no hiring to review
        given().accept(ContentType.JSON)
                .when().get("/hiring")
                .then().log().ifValidationFails().statusCode(200).body("$.size()", is(0));

    }
}
