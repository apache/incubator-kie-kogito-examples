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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

@Component
@ApplicationScope
public class ClassifierService {
    private static final Logger LOG = LoggerFactory.getLogger(ClassifierService.class);

    private Set<Country> classifiedCountries = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

    public JsonNode classifySmallMedium(JsonNode classifiedCountryNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Country classifiedCountry = mapper.treeToValue(classifiedCountryNode, Country.class);
            classifiedCountry.setClassifier("Small/Medium");
            classifiedCountries.add(classifiedCountry);
            JsonNode retNode = mapper.convertValue(classifiedCountry, JsonNode.class);

            return retNode;
        } catch(Exception e) {
            LOG.error("unable to classify country: " + classifiedCountryNode.toString());
            return classifiedCountryNode;
        }
    }

    public JsonNode classifyLarge(JsonNode classifiedCountryNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Country classifiedCountry = mapper.treeToValue(classifiedCountryNode, Country.class);
            classifiedCountry.setClassifier("Large");
            classifiedCountries.add(classifiedCountry);

            JsonNode retNode = mapper.convertValue(classifiedCountry, JsonNode.class);

            return retNode;
        } catch(Exception e) {
            LOG.error("unable to classify country: " + classifiedCountryNode.toString());
            return classifiedCountryNode;
        }
    }

    public Set<Country> getClassifiedCountries() {
        return classifiedCountries;
    }

}
