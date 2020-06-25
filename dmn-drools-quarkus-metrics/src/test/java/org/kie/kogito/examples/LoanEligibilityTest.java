/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.examples;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class LoanEligibilityTest {

    @Test
    public void testEvaluateTrafficViolation() {
        given()
                .body("{" +
                              "\"Client\": " +
                              "{\"age\": 43,\"salary\": 1950,\"existing payments\": 100}," +
                              "\"Loan\": {\"duration\": 15,\"installment\": 180}, " +
                              "\"SupremeDirector\" : \"Yes\", " +
                              "\"Bribe\": 1000" +
                              "}"
                )
                .contentType(ContentType.JSON)
                .when()
                .post("/LoanEligibility")
                .then()
                .statusCode(200)
                .body("'Decide'", is(true));
    }

    @Test
    public void testMetricsTrafficViolation() {
        given()
                .when()
                .get("/metrics")
                .then()
                .statusCode(200)
                .body(containsString("string_dmn_result{decision=\"Judgement\",endpoint=\"LoanEligibility\",identifier=\"Yes\",} 1.0"))
                .body(containsString("number_dmn_result{decision=\"Is Enought?\",endpoint=\"LoanEligibility\",quantile=\"0.1\",} 100.0"))
                .body(containsString("api_http_response_code{endpoint=\"LoanEligibility\",identifier=\"200\",} 1.0"));
    }
}
