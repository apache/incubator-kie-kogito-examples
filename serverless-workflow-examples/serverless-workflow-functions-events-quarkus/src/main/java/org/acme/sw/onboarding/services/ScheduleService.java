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

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.acme.sw.onboarding.model.Appointment;
import org.acme.sw.onboarding.model.Patient;

@ApplicationScoped
public class ScheduleService {

    public final static int FIRST_HOUR_MORNING = 9;

    /*
     * Our doctors can attend one patient per day only :)
     */
    private final Map<String, List<LocalDateTime>> schedule;

    public ScheduleService() {
        this.schedule = new ConcurrentHashMap<>();
    }

    /*
     * In a real world scenario this can get far more complex, we should have a transactions and sync access to the schedule.
     * We don't want to mess with our doctors schedule, right? :)
     * To make it simple and less error prone, we are using a simple Map to hold our data.
     */
    public Appointment createAppointment(final Patient patient) {
        final Appointment appointment = new Appointment();
        final String doctorId = patient.getAssignedDoctor().getId();
        appointment.setPatient(patient);
        // better case scenario we set an appointment today an hour from now :)
        appointment.setDate(LocalDateTime.now().plusHours(1));
        appointment.setDoctor(patient.getAssignedDoctor());

        // let's find room for our patient
        if (this.schedule.get(doctorId) != null) {
            final Optional<LocalDateTime> lastDate = this.schedule.get(doctorId).stream().max(Comparator.nullsFirst(Comparator.naturalOrder()));
            appointment.setDate(this.addOneDayFirstHourInMorning(lastDate.orElseThrow(IllegalStateException::new)));
        } else { // this doctor hasn't been set an appointment yet
            this.schedule.put(doctorId, new ArrayList<>());
        }
        this.schedule.get(doctorId).add(appointment.getDate());

        return appointment;
    }

    private LocalDateTime addOneDayFirstHourInMorning(final LocalDateTime targetDate) {
        return targetDate.plusDays(1).withHour(FIRST_HOUR_MORNING).withMinute(0);
    }
}
