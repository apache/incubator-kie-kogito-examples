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
package org.acme.sw.onboarding.queries;

import java.util.List;

import org.acme.sw.onboarding.model.Doctor;
import org.acme.sw.onboarding.model.Patient;
import org.acme.sw.onboarding.model.SymptomSpecialty;
import org.acme.sw.onboarding.services.DoctorService;
import org.acme.sw.onboarding.services.SymptomSpecialtyService;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignmentUnit implements RuleUnitData {

    private DataStore<Patient> patients;
    private DataStore<Doctor> doctors;
    private DataStore<SymptomSpecialty> symptomSpecialties;

    public AssignmentUnit() {
        this.patients = DataSource.createStore();
        this.doctors = DataSource.createStore();
        this.symptomSpecialties = DataSource.createStore();
        this.populate();
    }

    private void populate() {
        DoctorService.get().getDoctors().forEach(this.doctors::add);
        SymptomSpecialtyService.get().getSymptomSpecialties().forEach(this.symptomSpecialties::add);
    }

    public DataStore<Patient> getPatients() {
        return patients;
    }

    public void setPatients(DataStore<Patient> patients) {
        this.patients = patients;
    }

    public void addPatients(final List<Patient> patientList) {
        for (Patient p : patientList) {
            this.patients.add(p);
        }
    }

    @JsonIgnore
    public DataStore<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(DataStore<Doctor> doctors) {
        this.doctors = doctors;
    }

    @JsonIgnore
    public DataStore<SymptomSpecialty> getSymptomSpecialties() {
        return symptomSpecialties;
    }

    public void setSymptomSpecialties(DataStore<SymptomSpecialty> symptomSpecialties) {
        this.symptomSpecialties = symptomSpecialties;
    }
}
