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

import jakarta.inject.Inject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
