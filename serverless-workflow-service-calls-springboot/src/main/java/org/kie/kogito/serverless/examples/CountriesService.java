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
package org.kie.kogito.serverless.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class CountriesService {

    private static final Logger LOG = LoggerFactory.getLogger(CountriesService.class);

    @Autowired
    private RestTemplate restTemplate;

    public JsonNode getCountryInfo(JsonNode nameNode) {
        JsonNode retNode = null;

        try {

            ResponseEntity<Country[]> response =
                    restTemplate.getForEntity(
                            "https://restcountries.eu/rest/v2/name/{name}",
                            Country[].class, nameNode.get("name").asText());
            Country[] countries = response.getBody();
            Country country = countries[0]; // we just get the first for this example

            ObjectMapper mapper = new ObjectMapper();
            retNode = mapper.convertValue(country, JsonNode.class);

            // population is given as string, but jsonpath needs it as int to be able to compare
            String population = retNode.get("population").asText();
            ((ObjectNode)retNode).put("population", Integer.parseInt(population));

        } catch (RestClientException e) {
            LOG.error(e.getMessage());
        }

        return retNode;
    }
}
