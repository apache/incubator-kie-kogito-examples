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
package org.kogito.serverless.examples;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/countryclassifier")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CountriesClassifierResource {

    private static final Logger LOG = Logger.getLogger(CountriesClassifierResource.class);

    private Set<Country> classifiedCountries = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

    @GET
    public Set<Country> list() {
        return classifiedCountries;
    }

    public JsonNode classifySmallMedium(JsonNode classifiedCountryNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Country classifiedCountry = mapper.treeToValue(classifiedCountryNode.get("response").get(0), Country.class);
            classifiedCountry.setClassifier("Small/Medium");
            classifiedCountries.add(classifiedCountry);
            JsonNode retNode = mapper.convertValue(classifiedCountry, JsonNode.class);

            return retNode;
        } catch (Exception e) {
            LOG.error("unable to classify country: " + classifiedCountryNode.toString());
            return classifiedCountryNode;
        }
    }

    public JsonNode classifyLarge(JsonNode classifiedCountryNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Country classifiedCountry = mapper.treeToValue(classifiedCountryNode.get("response").get(0), Country.class);
            classifiedCountry.setClassifier("Large");
            classifiedCountries.add(classifiedCountry);

            JsonNode retNode = mapper.convertValue(classifiedCountry, JsonNode.class);

            return retNode;
        } catch (Exception e) {
            LOG.error("unable to classify country: " + classifiedCountryNode.toString());
            return classifiedCountryNode;
        }
    }
}
