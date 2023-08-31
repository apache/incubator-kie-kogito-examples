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
package org.acme.sw.onboarding.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.acme.sw.onboarding.model.SymptomSpecialty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SymptomSpecialtyService {

    private static final String SYMPTOMS_DATA_PATH = "/data/symptom_specialty.json";
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorService.class);
    private static final SymptomSpecialtyService INSTANCE = new SymptomSpecialtyService();
    private final List<SymptomSpecialty> symptomSpecialties;

    private SymptomSpecialtyService() {
        this.symptomSpecialties = new ArrayList<>();
        this.populate();
    }

    public static SymptomSpecialtyService get() {
        return INSTANCE;
    }

    private void populate() {
        try {
            List<SymptomSpecialty> symptomSpecialties = new ObjectMapper().readValue(this.getClass().getResourceAsStream(SYMPTOMS_DATA_PATH), new TypeReference<>() {
            });
            this.symptomSpecialties.addAll(symptomSpecialties);
            LOGGER.info("Predefined data from SymptomSpecialty have been populated");
        } catch (IOException ex) {
            throw new IllegalStateException("Problem while populating SymptomSpecialty with JSON predefined data", ex);
        }
    }

    public List<SymptomSpecialty> getSymptomSpecialties() {
        return this.symptomSpecialties;
    }
}
