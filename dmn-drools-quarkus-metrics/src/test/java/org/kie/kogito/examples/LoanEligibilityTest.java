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
package org.kie.kogito.examples;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class LoanEligibilityTest {

    @Test
    public void testEvaluateLoanEligibility() {
        // Approved Loan
        given()
                .body("{" +
                        "\"Client\": " +
                        "{\"age\": 43,\"salary\": 1950,\"existing payments\": 100}," +
                        "\"Loan\": {\"duration\": 15,\"installment\": 180}, " +
                        "\"SupremeDirector\" : \"Yes\", " +
                        "\"Bribe\": 1000" +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/LoanEligibility")
                .then()
                .statusCode(200)
                .body("'Decide'", is(true));

        // Not approved loan
        given()
                .body("{" +
                        "\"Client\": " +
                        "{\"age\": 43,\"salary\": 1950,\"existing payments\": 100}," +
                        "\"Loan\": {\"duration\": 15,\"installment\": 180}, " +
                        "\"SupremeDirector\" : \"No\", " +
                        "\"Bribe\": 0" +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/LoanEligibility")
                .then()
                .statusCode(200)
                .body("'Decide'", is(false));

        given()
                .when()
                .get("/metrics")
                .then()
                .statusCode(200)
                .body(containsString("string_dmn_result_total{decision=\"Eligibility\",endpoint=\"LoanEligibility\",identifier=\"Yes\",} 2.0"))
                .body(containsString("string_dmn_result_total{decision=\"Judgement\",endpoint=\"LoanEligibility\",identifier=\"Yes\",} 1.0"))
                .body(containsString("string_dmn_result_total{decision=\"Judgement\",endpoint=\"LoanEligibility\",identifier=\"No\",} 1.0"))
                .body(containsString("boolean_dmn_result_total{decision=\"Decide\",endpoint=\"LoanEligibility\",identifier=\"true\",} 1.0"))
                .body(containsString("boolean_dmn_result_total{decision=\"Decide\",endpoint=\"LoanEligibility\",identifier=\"false\",} 1.0\n"))
                .body(containsString("number_dmn_result{decision=\"Is Enough?\",endpoint=\"LoanEligibility\",quantile=\"0.5\",} 0.0"))
                .body(containsString("number_dmn_result_count{decision=\"Is Enough?\",endpoint=\"LoanEligibility\",} 2.0"))
                .body(containsString("number_dmn_result_sum{decision=\"Is Enough?\",endpoint=\"LoanEligibility\",} 100.0"))
                .body(containsString("number_dmn_result{decision=\"Is Enough?\",endpoint=\"LoanEligibility\",quantile=\"0.75\",} 100.0"))
                .body(containsString("number_dmn_result_max{decision=\"Is Enough?\",endpoint=\"LoanEligibility\",} 100.0"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDashboardsListIsAvailable() {
        List<String> dashboards = given().contentType(ContentType.JSON).accept(ContentType.JSON).when()
                .get("/monitoring/dashboards/list.json").as(List.class);

        Assertions.assertEquals(4, dashboards.size());
    }
}
