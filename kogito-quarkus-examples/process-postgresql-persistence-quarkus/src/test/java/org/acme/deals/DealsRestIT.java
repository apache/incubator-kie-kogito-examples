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
package org.acme.deals;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusIntegrationTest
public class DealsRestIT {

    @Test
    public void testDealsRest() {
        // test adding new deal
        String addDealPayload =
                "{\"name\" : \"my fancy deal\", \"traveller\" : { \"firstName\" : \"John\", \"lastName\" : \"Doe\", \"email\" : \"jon.doe@example.com\", \"nationality\" : \"American\",\"address\" : { \"street\" : \"main street\", \"city\" : \"Boston\", \"zipCode\" : \"10005\", \"country\" : \"US\" }}}";
        String dealId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addDealPayload)
                .when().post("/deals")
                .then().log().ifValidationFails().statusCode(201).body("id", notNullValue()).extract().path("id");

        // test getting the created deal
        given().accept(ContentType.JSON)
                .when().get("/deals")
                .then().log().ifValidationFails().statusCode(200).body("size()", is(1), "[0].id", is(dealId));

        // test getting order by id
        given().accept(ContentType.JSON)
                .when().get("/deals/" + dealId)
                .then().log().ifValidationFails().statusCode(200).body("id", is(dealId));

        // get deals for review
        String dealReviewId = given().accept(ContentType.JSON)
                .when().get("/dealreviews")
                .then().log().ifValidationFails().statusCode(200).body("size()", is(1)).body("[0].id", notNullValue()).extract().path("[0].id");

        // get task for john
        String taskId = given().accept(ContentType.JSON)
                .when().get("/dealreviews/{uuid}/tasks?user=john", dealReviewId)
                .then().log().ifValidationFails().statusCode(200).body("size()", is(1)).extract().path("[0].id");

        // complete review task
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body("{\"review\" : \"very good work\"}")
                .when().post("/dealreviews/{uuid}/review/{tuuid}?user=john", dealReviewId, taskId)
                .then().log().ifValidationFails().statusCode(200);

        //verify no deals to review
        given().accept(ContentType.JSON)
                .when().get("/dealreviews")
                .then().log().ifValidationFails().statusCode(200).body("size()", is(0));

        //verify no deals
        given().accept(ContentType.JSON)
                .when().get("/deals")
                .then().log().ifValidationFails().statusCode(200).body("size()", is(0));
    }
}
