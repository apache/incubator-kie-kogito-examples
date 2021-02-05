/*
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
package org.kie.pmml.kogito.quarkus.example;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class SimpleScorecardTest {

    @Test
    public void testEvaluateSimpleScorecard() {
        String inputData = "{\"input1\":5.0, \"input2\":-10.0}";
        Object resultVariables = given()
                .contentType(ContentType.JSON)
                .body(inputData)
                .when()
                .post("/SimpleScorecard")
                .then()
                .statusCode(200)
                .body("correlationId", is(new IsNull()))
                .body("segmentationId", is(new IsNull()))
                .body("segmentId", is(new IsNull()))
                .body("segmentIndex", is(0)) // as JSON is not schema aware, here we assert the RAW string
                .body("resultCode", is("OK"))
                .body("resultObjectName", is("score"))
                .extract()
                .path("resultVariables");
        assertNotNull(resultVariables);
        assertTrue(resultVariables instanceof Map);
        Map<String, Object> mappedResultVariables = (Map) resultVariables;
        assertTrue(mappedResultVariables.containsKey("score"));
        assertEquals(-15.0f, mappedResultVariables.get("score"));
        assertTrue(mappedResultVariables.containsKey("Score"));
        assertEquals(-15.0f, mappedResultVariables.get("Score"));
        assertTrue(mappedResultVariables.containsKey("Reason Code 1"));
        assertEquals("Input1ReasonCode", mappedResultVariables.get("Reason Code 1"));
        assertTrue(mappedResultVariables.containsKey("Reason Code 2"));
        assertEquals("Input2ReasonCode", mappedResultVariables.get("Reason Code 2"));
    }
}
