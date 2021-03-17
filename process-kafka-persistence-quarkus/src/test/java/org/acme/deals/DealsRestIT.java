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
package org.acme.deals;

import javax.inject.Inject;

import org.apache.kafka.streams.KafkaStreams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class DealsRestIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Inject
    KafkaStreams streams;

    @BeforeEach
    public void setup() {
        streams.cleanUp();
    }

    @Test
    public void testDealsRest() {
        // test adding new deal
        String deal = "my fancy deal";
        String addDealPayload = "{\"name\" : \"" + deal
                + "\", \"traveller\" : { \"firstName\" : \"John\", \"lastName\" : \"Doe\", \"email\" : \"jon.doe@example.com\", \"nationality\" : \"American\",\"address\" : { \"street\" : \"main street\", \"city\" : \"Boston\", \"zipCode\" : \"10005\", \"country\" : \"US\" }}}";
        String dealId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addDealPayload)
                .when().post("/deals")
                .then().statusCode(201).body("id", notNullValue()).extract().path("id");

        // test getting the created deal
        given().accept(ContentType.JSON)
                .when().get("/deals")
                .then().statusCode(200)
                .body("$.size()", is(1))
                .body("[0].id", is(dealId))
                .body("[0].name", is(deal));

        // test getting order by id
        given().accept(ContentType.JSON)
                .when().get("/deals/" + dealId)
                .then().statusCode(200).body("id", is(dealId));

        // get deals for review
        String dealReviewId = given().accept(ContentType.JSON)
                .when().get("/dealreviews")
                .then().statusCode(200)
                .body("$.size()", is(1))
                .body("[0].id", notNullValue())
                .body("[0].deal", is(deal))
                .extract().path("[0].id");

        // get task for john
        String taskId = given().accept(ContentType.JSON)
                .when().get("/dealreviews/{uuid}/tasks?user=john", dealReviewId)
                .then().statusCode(200)
                .body("$.size", is(1))
                .body("[0].name", is("review"))
                .body("[0].parameters.deal", is(deal))
                .extract().path("[0].id");

        // complete review task
        String review = "very good work";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body("{\"review\" : \"" + review + "\"}")
                .when().post("/dealreviews/{uuid}/review/{tuuid}?user=john", dealReviewId, taskId)
                .then().statusCode(200)
                .body("review", is(review))
                .body("deal", is(deal));

        //verify no deals to review
        given().accept(ContentType.JSON)
                .when().get("/dealreviews")
                .then().statusCode(200).body("$.size()", is(0));

        //verify no deals
        given().accept(ContentType.JSON)
                .when().get("/deals")
                .then().statusCode(200).body("$.size()", is(0));
    }
}
