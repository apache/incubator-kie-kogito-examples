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
package org.kogito.serverless.examples;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CountriesResourceTest {

    @Inject
    CountriesResource resource;

    @Test
    public void testCountryNameEndpoint() {
        given()
                .when().get("/country/name/greece")
                .then()
                .statusCode(200)
                .body("alpha2Code", is("GR"),
                        "capital", is("Athens"),
                        "region", is("Europe"),
                        "currencies.size()", is(1),
                        "currencies[0].name", is("Euro"));
    }

    @Test
    public void testJavaServiceCall() {
        Country country = resource.name("greece");
        assertNotNull(country);
        assertEquals("Greece", country.getName());
        assertEquals("Europe", country.getRegion());
        assertEquals("Euro", country.getCurrencies().get(0).getName());
    }

    @Test
    public void testJavaServiceCallWithJsonNode() throws Exception {
        String jsonString = "{\"name\": \"greece\"}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode inputNode = mapper.readTree(jsonString);

        JsonNode retNode = resource.jsonName(inputNode);
        assertNotNull(retNode);
        assertEquals("Greece", retNode.get("name").textValue());
        assertEquals("Europe", retNode.get("region").textValue());
        assertEquals("Euro", retNode.get("currencies").get(0).get("name").textValue());
    }
}
