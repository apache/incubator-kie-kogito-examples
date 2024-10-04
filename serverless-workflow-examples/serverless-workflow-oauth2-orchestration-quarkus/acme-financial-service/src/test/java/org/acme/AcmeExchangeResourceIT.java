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
package org.acme;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.http.ContentType;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@QuarkusIntegrationTest
class AcmeExchangeResourceIT {

    private static final String EUR = "EUR";
    private static final String USD = "USD";
    private static final String CAD = "CAD";
    private static final String EXCHANGE_DATE = "2022-06-10";
    private static String FINANCIAL_RESOURCE_EXCHANGE_RATE_URL = "/financial-service/exchange-rate";

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }

    @Test
    void unauthorizedTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get(FINANCIAL_RESOURCE_EXCHANGE_RATE_URL)
                .then()
                .statusCode(401);
    }

    @ParameterizedTest
    @MethodSource("testParams")
    void exchangeRate(String currencyFrom, String currencyTo, String exchangeDate, String expectedRate) {
        // execute the exchange-rate query and check the result.
        String expectedResponse = "{\"rate\":" + expectedRate + "}";
        String response = given()
                .auth().oauth2(getAccessToken("alice"))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("currencyFrom", currencyFrom)
                .queryParam("currencyTo", currencyTo)
                .queryParam("exchangeDate", exchangeDate)
                .get(FINANCIAL_RESOURCE_EXCHANGE_RATE_URL)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .body()
                .asString();
        assertThat(response).isEqualTo(expectedResponse);
    }

    private static Stream<Arguments> testParams() {
        return Stream.of(
                Arguments.of(USD, CAD, EXCHANGE_DATE, "1.2747211193042163"),
                Arguments.of(CAD, USD, EXCHANGE_DATE, "0.7844853159299912"),
                Arguments.of(EUR, USD, EXCHANGE_DATE, "1.0578"));
    }
}
