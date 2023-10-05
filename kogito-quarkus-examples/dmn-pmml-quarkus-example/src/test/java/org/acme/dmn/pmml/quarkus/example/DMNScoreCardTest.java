/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.acme.dmn.pmml.quarkus.example;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class DMNScoreCardTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testEvaluateMiningModelDMN() {
        String inputData = "{\"age\": 23.0, \"occupation\": \"SKYDIVER\", \"residenceState\": \"AP\", \"validLicense\": true}";
        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE)))
                .contentType(ContentType.JSON)
                .body(inputData)
                .when()
                .post("/TestScoreCardDMN")
                .then()
                .statusCode(200)
                .body("ScoreCardBKM", is("function ScoreCardBKM( age, occupation, residenceState, validLicense )"))
                .body("age", is(comparesEqualTo(23))) // was input
                .body("occupation", is(comparesEqualTo("SKYDIVER"))) // was input
                .body("residenceState", is(comparesEqualTo("AP"))) // was input
                .body("validLicense", is(comparesEqualTo(true))) // was input
                .body("Score", is(comparesEqualTo(21.345))) // real decision output
        ;
    }
}
