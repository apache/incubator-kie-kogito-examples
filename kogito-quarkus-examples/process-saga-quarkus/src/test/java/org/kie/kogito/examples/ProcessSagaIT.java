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

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class ProcessSagaIT {

    public static final String ORDER_ID = "03e6cf79-3301-434b-b5e1-d6899b5639aa";

    @Test
    public void testOrderSuccess() {
        String payload = "{\n" +
                "    \"orderId\": \"" + ORDER_ID + "\"\n" +
                "}";
        ExtractableResponse<Response> response = createOrder(payload);
        response.path("id");
        assertThat(response.<String> path("paymentResponse.type")).isEqualTo("SUCCESS");
        assertThat(response.<String> path("stockResponse.type")).isEqualTo("SUCCESS");
        assertThat(response.<String> path("shippingResponse.type")).isEqualTo("SUCCESS");
        assertThat(response.<String> path("orderResponse.type")).isEqualTo("SUCCESS");
        assertThat(response.<String> path("orderResponse.resourceId")).isEqualTo(ORDER_ID);
    }

    @Test
    public void testOrderFailure() {
        String payload = "{\n" +
                "    \"orderId\": \"" + ORDER_ID + "\",\n" +
                "    \"failService\" : \"ShippingService\"\n" +
                "}";
        ExtractableResponse<Response> response = createOrder(payload);
        response.path("id");
        assertThat(response.<String> path("stockResponse.type")).isEqualTo("SUCCESS");
        assertThat(response.<String> path("paymentResponse.type")).isEqualTo("SUCCESS");
        assertThat(response.<String> path("shippingResponse.type")).isEqualTo("ERROR");
        assertThat(response.<String> path("orderResponse.type")).isEqualTo("ERROR");
        assertThat(response.<String> path("orderResponse.resourceId")).isEqualTo(ORDER_ID);
    }

    private ExtractableResponse<Response> createOrder(String payload) {
        ExtractableResponse<Response> response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .post("/order")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .extract();
        return response;
    }
}
