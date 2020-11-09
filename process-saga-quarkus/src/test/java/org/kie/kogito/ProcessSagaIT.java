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
package org.kie.kogito;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class ProcessSagaIT {

    public static final String TRIP_ID = "03e6cf79-3301-434b-b5e1-d6899b5639aa";

    @Test
    public void testTripSuccess() {
        String payload = "{\n" +
                "    \"tripId\": \"" + TRIP_ID + "\"\n" +
                "}";
        ExtractableResponse<Response> response = createTrip(payload);
        response.path("id");
        assertThat(response.<String>path("paymentResponse.type")).isEqualTo("SUCCESS");
        assertThat(response.<String>path("hotelResponse.type")).isEqualTo("SUCCESS");
        assertThat(response.<String>path("flightResponse.type")).isEqualTo("SUCCESS");
        assertThat(response.<String>path("tripResponse.type")).isEqualTo("SUCCESS");
        assertThat(response.<String>path("tripResponse.resourceId")).isEqualTo(TRIP_ID);
    }

    @Test
    public void testTripFailure() {
        String payload = "{\n" +
                "    \"tripId\": \"" + TRIP_ID + "\",\n" +
                "    \"failService\" : \"PaymentService\"\n" +
                "}";
        ExtractableResponse<Response> response = createTrip(payload);
        response.path("id");
        assertThat(response.<String>path("hotelResponse.type")).isEqualTo("SUCCESS");
        assertThat(response.<String>path("flightResponse.type")).isEqualTo("SUCCESS");
        assertThat(response.<String>path("paymentResponse.type")).isEqualTo("ERROR");
        assertThat(response.<String>path("tripResponse.type")).isEqualTo("ERROR");
        assertThat(response.<String>path("tripResponse.resourceId")).isEqualTo(TRIP_ID);
    }

    private ExtractableResponse<Response> createTrip(String payload) {
        ExtractableResponse<Response> response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .post("/trip")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .extract();
        return response;
    }
}
