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
package org.kogito.serverless.examples;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.kogito.serverless.examples.input.Country;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class FunctionsIT {

    @Test
    public void testCountriesFunction() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        given()
                .body(mapper.writeValueAsString(new Country("Germany")))
                .when().post("/country")
                .then()
                .statusCode(200)
                .body("name", CoreMatchers.equalTo("Germany"));

    }

    @Test
    public void testPopulationFunction() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        given()
                .body(mapper.writeValueAsString(new Country("USA")))
                .when().post("/population")
                .then()
                .statusCode(200)
                .body("name", CoreMatchers.equalTo("USA"));

    }

    @Test
    public void testClassificationFunction() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        given()
                .body(mapper.writeValueAsString(new Country("Serbia")))
                .when().post("/classify")
                .then()
                .statusCode(200)
                .body("name", CoreMatchers.equalTo("Serbia"));

    }
}
