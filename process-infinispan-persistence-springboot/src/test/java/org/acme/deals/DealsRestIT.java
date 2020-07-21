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
package org.acme.deals;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.springboot.InfinispanSpringBootTestResource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = InfinispanSpringBootTestResource.class)
public class DealsRestIT {

    @LocalServerPort
    int randomServerPort;
    
    @BeforeEach
    public void setup() {
        RestAssured.port = randomServerPort;
    }

    @Test
    public void testDealsRest() {
        // test adding new deal
        String addDealPayload = "{\"name\" : \"my fancy deal\", \"traveller\" : { \"firstName\" : \"John\", \"lastName\" : \"Doe\", \"email\" : \"jon.doe@example.com\", \"nationality\" : \"American\",\"address\" : { \"street\" : \"main street\", \"city\" : \"Boston\", \"zipCode\" : \"10005\", \"country\" : \"US\" }}}";
        String dealId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addDealPayload)
                .when().post("/deals")
                .then().log().ifValidationFails().statusCode(200).body("id", notNullValue()).extract().path("id");

        // test getting the created deal
        given().accept(ContentType.JSON)
                .when().get("/deals")
                .then().log().ifValidationFails().statusCode(200).body("$.size()", is(1), "[0].id", is(dealId));

        // test getting order by id
        given().accept(ContentType.JSON)
                .when().get("/deals/" + dealId)
                .then().log().ifValidationFails().statusCode(200).body("id", is(dealId));

        // get deals for review
        String dealReviewId = given().accept(ContentType.JSON)
                .when().get("/dealreviews")
                .then().log().ifValidationFails().statusCode(200).body("$.size()", is(1)).body("[0].id", notNullValue()).extract().path("[0].id");

        // get task for john
        Map<String, String> tasks = given().accept(ContentType.JSON)
                .when().get("/dealreviews/{uuid}/tasks?user=john", dealReviewId)
                .then().log().ifValidationFails().statusCode(200).extract().as(Map.class);
        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        // complete review task
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body("{\"review\" : \"very good work\"}")
                .when().post("/dealreviews/{uuid}/review/{tuuid}?user=john", dealReviewId, tasks.keySet().iterator().next())
                .then().log().ifValidationFails().statusCode(200);

        //verify no deals to review
        given().accept(ContentType.JSON)
                .when().get("/dealreviews")
                .then().log().ifValidationFails().statusCode(200).body("$.size()", is(0));

        //verify no deals
        given().accept(ContentType.JSON)
                .when().get("/deals")
                .then().log().ifValidationFails().statusCode(200).body("$.size()", is(0));
    }
}