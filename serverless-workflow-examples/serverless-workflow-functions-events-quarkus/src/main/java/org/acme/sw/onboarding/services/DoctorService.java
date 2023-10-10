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

import org.acme.sw.onboarding.model.Doctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DoctorService {

    private static final String DOCTOR_DATA_PATH = "/data/doctors.json";
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorService.class);
    private static final DoctorService INSTANCE = new DoctorService();
    private final List<Doctor> doctors;

    public DoctorService() {
        this.doctors = new ArrayList<>();
        this.populate();
    }

    public static DoctorService get() {
        return INSTANCE;
    }

    private void populate() {
        try {
            List<Doctor> doctors = new ObjectMapper().readValue(this.getClass().getResourceAsStream(DOCTOR_DATA_PATH), new TypeReference<>() {
            });
            this.doctors.addAll(doctors);
            LOGGER.info("Predefined data from Doctors have been populated");
        } catch (IOException ex) {
            throw new IllegalStateException("Problem while populating DoctorService with JSON predefined data", ex);
        }
    }

    public List<Doctor> getDoctors() {
        return this.doctors;
    }
}
