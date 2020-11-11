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
package org.acme.sw.onboarding.model;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class GenderTest {

    @Inject
    ObjectMapper mapper;

    @Test
    public void verifyJacksonEnumTransformation() throws JsonProcessingException {
        final String json = "{ \"name\": \"Mick\", \"dateOfBirth\": \"2017-08-15\", \"gender\": \"male\"}";
        final Patient patient = mapper.readValue(json, Patient.class);
        assertNotNull(patient);
        assertEquals(Gender.MALE, patient.getGender());
    }
}