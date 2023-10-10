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
package org.acme.sw.onboarding.resources;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.sw.onboarding.model.Appointment;
import org.acme.sw.onboarding.model.Error;
import org.acme.sw.onboarding.model.Patient;
import org.acme.sw.onboarding.services.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/onboarding/schedule/appointment")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AppointmentResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentResource.class);
    private final List<Appointment> appointments = new ArrayList<>();

    @Inject
    ScheduleService scheduleService;

    @POST
    public Response schedulePatientAppointment(@NotNull final Patient patient) {
        LOGGER.debug("Receive patient to schedule appointments: {}", patient);
        if (patient.getId() == null || patient.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new Error("Patient has not been processed! Patients need to have an ID."))
                    .build();
        }
        if (patient.getAssignedDoctor() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new Error("Doctor has not been assigned! Impossible to schedule an appointment. Please assign a doctor first."))
                    .build();
        }
        // now we can go
        this.appointments.add(scheduleService.createAppointment(patient));
        LOGGER.debug("Processed patient: {}", patient);
        return Response.ok(patient).build();
    }

    @GET
    public List<Appointment> getAppointmentCalendar() {
        return this.appointments;
    }

    @GET
    @Path("/doctor/{id}")
    public List<Appointment> getScheduleForDoctor(@NotEmpty @PathParam("id") final String doctorId) {
        return this.appointments.stream().filter(a -> a.getDoctor().getId().equals(doctorId)).collect(Collectors.toList());
    }

    @GET
    @Path("/patient/{id}")
    public List<Appointment> getScheduleForPatient(@NotEmpty @PathParam("id") final String patientId) {
        return this.appointments.stream()
                .filter(a -> a.getPatient().getId().equals(patientId) && a.getDate().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }
}
