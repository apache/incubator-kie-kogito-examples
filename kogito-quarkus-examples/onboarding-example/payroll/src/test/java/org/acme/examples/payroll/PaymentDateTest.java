/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.acme.examples.payroll;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class PaymentDateTest {

    @Test
    public void testEvaluatePaymentDateUS() {
        evaluateForCountry("US", "2019-05-15T23:59:00.123+00:00");
    }

    @Test
    public void testEvaluatePaymentDateUK() {
        evaluateForCountry("UK", "2019-05-10T23:59:00.123+00:00");
    }

    @Test
    public void testEvaluatePaymentDateDefault() {
        evaluateForCountry("aoc", "2019-05-01T23:59:00.123+00:00");
    }

    private void evaluateForCountry(String country, String result) {
        given()
                .body("{\"employee\" : {\"firstName\" : \"Mark\", \"lastName\" : \"Test\", \"personalId\" : \"xxx-yy-zzz\", \"birthDate\" : \"1995-12-10T14:50:12.123+02:00\", \"address\" : {\"country\" : \""
                        + country + "\", \"city\" : \"Boston\", \"street\" : \"any street 3\", \"zipCode\" : \"10001\"}}}")
                .contentType(ContentType.JSON)
                .when()
                .post("/payments/date")
                .then()
                .statusCode(200)
                .body("paymentDate", is(result));
    }
}
