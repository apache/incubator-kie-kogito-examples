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

package org.kie.dmn.submarine.quarkus.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TrafficViolationTest {

    @Test
    public void testGET() {
        given()
          .when()
               .get("/")
          .then()
             .statusCode(200)
               .body("models", hasSize(greaterThanOrEqualTo(1)));
    }
    
    @Test
    public void testEvaluateTrafficViolation() {
        given()
               .body("{\n" +
                     "    \"Driver\": {\n" +
                     "        \"Points\": 2\n" +
                     "    },\n" +
                     "    \"Violation\": {\n" +
                     "        \"Type\": \"speed\",\n" +
                     "        \"Actual Speed\": 120,\n" +
                     "        \"Speed Limit\": 100\n" +
                     "    }\n" +
                     "}")
               .contentType(ContentType.JSON)
          .when()
               //               .header("X-DMN-model-namespace", "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF")
               //               .header("X-DMN-model-name", "Traffic Violation")
               .post("/")
          .then()
             .statusCode(200)
               .body("'dmn-context'.'Should the driver be suspended?'", is("No"))
               .body("decision-results", hasItem(allOf(hasEntry("decision-name", "Should the driver be suspended?"),
                                                       hasEntry("result", "No"))));
    }
}
