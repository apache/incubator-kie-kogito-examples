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

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import org.acme.sw.onboarding.model.Appointment;
import org.acme.sw.onboarding.model.Doctor;
import org.acme.sw.onboarding.model.Patient;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ScheduleServiceTest {

    @Inject
    ScheduleService scheduleService;

    @Test
    void verifyAppointments() {
        final List<Doctor> doctors = DoctorService.get().getDoctors();
        // Michael and Mark are assigned to the same doctor, let's see the outcome.
        final LocalDateTime beforeAppointment = LocalDateTime.now();
        final Patient michael = new Patient();
        michael.setName("Michael");
        michael.setAssignedDoctor(doctors.get(0));
        michael.setId("1");
        final Patient mark = new Patient();
        mark.setName("Mark");
        mark.setAssignedDoctor(doctors.get(0));
        mark.setId("2");

        final Appointment michaelAppointment = scheduleService.createAppointment(michael);
        assertNotNull(michaelAppointment);
        assertEquals(michaelAppointment.getDoctor(), michael.getAssignedDoctor());
        assertNotNull(michaelAppointment.getDate());

        final Appointment markAppointment = scheduleService.createAppointment(mark);
        assertNotNull(markAppointment);
        assertEquals(markAppointment.getDoctor(), mark.getAssignedDoctor());
        assertNotNull(markAppointment.getDate());

        assertTrue(markAppointment.getDate().isAfter(michaelAppointment.getDate()));
        assertEquals(ScheduleService.FIRST_HOUR_MORNING, markAppointment.getDate().getHour());
        assertTrue(michaelAppointment.getDate().isAfter(beforeAppointment));
    }
}
