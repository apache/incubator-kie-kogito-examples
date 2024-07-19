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
package org.kie.kogito.examples.quarkus;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusIntegrationTest
public class DroolsMetricsIT {

    private static final String PROJECT_VERSION = ProjectMetadataProvider.getProjectVersion();
    private static final String PROJECT_ARTIFACT_ID = ProjectMetadataProvider.getProjectArtifactId();

    @Test
    public void testDrlMetrics() {
        given()
                .body("{\"strings\": [\"hello\"]}")
                .contentType(ContentType.JSON)
                .when()
                .post("/hello")
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/q/metrics")
                .then()
                .statusCode(200)
                .body(containsString(
                        String.format("drl_match_fired_nanosecond_count{app_id=\"default-rule-monitoring-listener\",artifactId=\"%s\",rule=\"helloWorld\",version=\"%s\"} 1.0", PROJECT_ARTIFACT_ID,
                                PROJECT_VERSION)));

        given()
                .when()
                .get("/q/metrics")
                .then()
                .statusCode(200)
                .body(containsString("org_kie_kogito_examples_customruleeventlistener_total{event=\"afteractivationfiredeventimpl" +
                        "\"} 1.0"));
        given()
                .when()
                .get("/q/metrics")
                .then()
                .statusCode(200)
                .body(containsString("org_kie_kogito_examples_customruleeventlistener_total{event=\"beforeactivationfiredeventimpl" +
                        "\"} 1.0"));
        given()
                .when()
                .get("/q/metrics")
                .then()
                .statusCode(200)
                .body(containsString("org_kie_kogito_examples_customruleeventlistener_total{event=\"activationcreatedeventimpl" +
                        "\"} 1.0"));
    }
}
