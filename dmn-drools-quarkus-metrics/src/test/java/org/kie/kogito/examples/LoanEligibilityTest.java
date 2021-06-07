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

    private static final String PROJECT_VERSION = ProjectMetadataProvider.getProjectVersion();
    private static final String PROJECT_ARTIFACT_ID = ProjectMetadataProvider.getProjectArtifactId();

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
                .body(containsString(
                        String.format("string_dmn_result_total{artifactId=\"%s\",decision=\"Eligibility\",endpoint=\"LoanEligibility\",identifier=\"Yes\",version=\"%s\",} 2.0", PROJECT_ARTIFACT_ID,
                                PROJECT_VERSION)))
                .body(containsString(
                        String.format("string_dmn_result_total{artifactId=\"%s\",decision=\"Judgement\",endpoint=\"LoanEligibility\",identifier=\"Yes\",version=\"%s\",} 1.0", PROJECT_ARTIFACT_ID,
                                PROJECT_VERSION)))
                .body(containsString(
                        String.format("string_dmn_result_total{artifactId=\"%s\",decision=\"Judgement\",endpoint=\"LoanEligibility\",identifier=\"No\",version=\"%s\",} 1.0", PROJECT_ARTIFACT_ID,
                                PROJECT_VERSION)))
                .body(containsString(
                        String.format("boolean_dmn_result_total{artifactId=\"%s\",decision=\"Decide\",endpoint=\"LoanEligibility\",identifier=\"true\",version=\"%s\",} 1.0", PROJECT_ARTIFACT_ID,
                                PROJECT_VERSION)))
                .body(containsString(
                        String.format("boolean_dmn_result_total{artifactId=\"%s\",decision=\"Decide\",endpoint=\"LoanEligibility\",identifier=\"false\",version=\"%s\",} 1.0\n", PROJECT_ARTIFACT_ID,
                                PROJECT_VERSION)))
                .body(containsString(
                        String.format("number_dmn_result{artifactId=\"%s\",decision=\"Is Enough?\",endpoint=\"LoanEligibility\",version=\"%s\",quantile=\"0.5\",} 0.0", PROJECT_ARTIFACT_ID,
                                PROJECT_VERSION)))
                .body(containsString(
                        String.format("number_dmn_result_max{artifactId=\"%s\",decision=\"Is Enough?\",endpoint=\"LoanEligibility\",version=\"%s\",} 100.0", PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format("number_dmn_result_count{artifactId=\"%s\",decision=\"Is Enough?\",endpoint=\"LoanEligibility\",version=\"%s\",} 2.0", PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format("number_dmn_result_sum{artifactId=\"%s\",decision=\"Is Enough?\",endpoint=\"LoanEligibility\",version=\"%s\",} 100.0", PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format("number_dmn_result{artifactId=\"%s\",decision=\"Is Enough?\",endpoint=\"LoanEligibility\",version=\"%s\",quantile=\"0.75\",} 100.0", PROJECT_ARTIFACT_ID,
                                PROJECT_VERSION)))
                .body(containsString(
                        String.format("api_execution_elapsed_seconds{artifactId=\"%s\",endpoint=\"LoanEligibility\",version=\"%s\",quantile=\"0.5\",}", PROJECT_ARTIFACT_ID, PROJECT_VERSION)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDashboardsListIsAvailable() {
        List<String> dashboards = given().contentType(ContentType.JSON).accept(ContentType.JSON).when()
                .get("/monitoring/dashboards/list.json").as(List.class);

        Assertions.assertEquals(4, dashboards.size());
    }
}
