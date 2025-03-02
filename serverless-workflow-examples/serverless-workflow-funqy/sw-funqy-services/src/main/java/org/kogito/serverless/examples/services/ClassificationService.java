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
package org.kogito.serverless.examples.services;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import org.kogito.serverless.examples.input.Country;

@ApplicationScoped
public class ClassificationService {

    private Map<String, String> classifications = new HashMap<>();

    public ClassificationService() {
        classifications.put("Brazil", "Large");
        classifications.put("USA", "Large");
        classifications.put("Serbia", "Small");
        classifications.put("Germany", "Medium");
        classifications.put("N/A", "N/A");
    }

    public Country getClassification(Country country) {
        country.setClassifier(classifications.get(country.getName()));
        return country;
    }
}
