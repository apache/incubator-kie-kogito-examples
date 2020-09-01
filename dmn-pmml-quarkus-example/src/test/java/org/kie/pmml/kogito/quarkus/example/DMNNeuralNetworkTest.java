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
package org.kie.pmml.kogito.quarkus.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class DMNNeuralNetworkTest {
    
    @Test
    public void testEvaluateNeuralNetworkDMN() {
        String inputData = "{\"Age\":40, " +
                "\"Employment\":\"Private\", " +
                "\"Education\":\"College\", " +
                "\"Marital\":\"Married\", " +
                "\"Occupation\":\"Service\", " +
                "\"Income\":324035.50, " +
                "\"Gender\":\"Male\", " +
                "\"Deductions\":2340, " +
                "\"Hours\":48 }";
        given()
                .contentType(ContentType.JSON)
                .body(inputData)
                .when()
                .post("/TestNeuralNetworkBKM")
                .then()
                .statusCode(200)
                .body("NeuralNetworkBKM", is("function NeuralNetworkBKM( Marital, Gender, Employment, Income, Occupation, Education, Deductions, Age )"))
                .body("Age", is(Integer.valueOf("40")))
                .body("Employment", is("Private"))
                .body("Education", is("College"))
                .body("Marital", is("Married"))
                .body("Occupation", is("Service"))
                .body("Income", is(Float.valueOf("324035.50")))
                .body("Gender", is("Male"))
                .body("Deductions", is(Integer.valueOf("2340")))
                .body("Hours", is(Integer.valueOf("48")))
                .body("Decision", is(Float.valueOf("0.11968884558738997")));
    }
}
