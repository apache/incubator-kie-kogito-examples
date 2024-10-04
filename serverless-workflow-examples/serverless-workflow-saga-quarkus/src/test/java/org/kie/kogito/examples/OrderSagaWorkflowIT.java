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
package org.kie.kogito.examples;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
public class OrderSagaWorkflowIT {

    public static final String ORDER_ID = "03e6cf79-3301-434b-b5e1-d6899b5639aa";
    public static final String PATH = "/order_saga_error_workflow";

    @Test
    public void testOrderSuccess() {
        String payload = String.format("{\n" +
                                               " \"orderId\": \"%s\",\n" +
                                               " \"failService\" : \"%s\"\n" +
                                               "}", ORDER_ID, "blah");
        ValidatableResponse response = createOrder(payload);
        response.body("workflowdata.orderResponse.type", equalTo("SUCCESS"));
        response.body("workflowdata.orderId", equalTo(ORDER_ID));
    }

    @Test
    public void testOrderFailure() {
        String payload = String.format("{\n" +
                "  \"orderId\": \"%s\",\n" +
                "  \"failService\" : \"%s\"\n" +
                "}", ORDER_ID, "ShippingService");
        ValidatableResponse response = createOrder(payload);
        response.body("workflowdata.orderResponse.type", equalTo("ERROR"));
        response.body("workflowdata.orderId", equalTo(ORDER_ID));
    }

    private ValidatableResponse createOrder(String payload) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .header("Location", notNullValue());
    }
}
