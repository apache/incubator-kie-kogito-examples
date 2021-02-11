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
package org.kie.kogito.serverless.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = ServerlessServiceCallsExampleApplication.class)
@RunWith(SpringRunner.class)
public class CountriesServiceIntegrationTest {

    @Autowired
    CountriesService countriesService;

    @Test
    public void testJavaServiceCall() throws Exception {

        assertNotNull(countriesService);

        String sampleDataStr = "{\"name\": \"Greece\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode sampleData = mapper.readTree(sampleDataStr);

        JsonNode results = countriesService.getCountryInfo(sampleData);
        assertNotNull(results);
        assertEquals("Greece", results.get("name").asText());
        assertEquals("Europe", results.get("region").asText());
    }
}
