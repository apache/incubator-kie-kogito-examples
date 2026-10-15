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
package org.kie.kogito.dmn.pmml.quarkus.example;

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
public class DMNEquipmentMaintenanceTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testEvaluateEquipmentMaintenanceDMN() {
        String inputData = "{\"monthsInService\": 23.0, \"equipmentType\": \"PRESS\", \"operatingEnvironment\": \"OUTDOOR\", \"inspectionPassed\": true}";
        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE)))
                .contentType(ContentType.JSON)
                .body(inputData)
                .when()
                .post("/EquipmentMaintenance")
                .then()
                .statusCode(200)
                .body("EquipmentMaintenanceScoreBKM", is("function EquipmentMaintenanceScoreBKM( monthsInService, equipmentType, operatingEnvironment, inspectionPassed )"))
                .body("monthsInService", is(comparesEqualTo(23))) // was input
                .body("equipmentType", is(comparesEqualTo("PRESS"))) // was input
                .body("operatingEnvironment", is(comparesEqualTo("OUTDOOR"))) // was input
                .body("inspectionPassed", is(comparesEqualTo(true))) // was input
                .body("'Maintenance Priority'", is(comparesEqualTo(21.345))) // real decision output
        ;
    }
}
