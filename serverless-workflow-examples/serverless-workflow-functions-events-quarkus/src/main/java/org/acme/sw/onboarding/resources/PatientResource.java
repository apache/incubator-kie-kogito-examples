/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.sw.onboarding.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

import org.acme.sw.onboarding.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/onboarding/patient")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PatientResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientResource.class);
    /*
     * In a real world we would have a Database.
     * See: https://quarkus.io/guides/hibernate-orm-panache
     */
    private final List<Patient> patients;

    public PatientResource() {
        this.patients = new ArrayList<>();
    }

    @POST
    public Response storeNewPatient(@NotNull final Patient patient) {
        LOGGER.debug("Received patient to store in the internal in memory database: {}", patient);
        patient.setId(UUID.randomUUID().toString());
        patients.add(patient);
        LOGGER.debug("Patient has been stored in the internal memory: {}", patient);
        return Response.ok(patient).build();
    }

    @GET
    public List<Patient> getPatients() {
        return this.patients;
    }

    @GET
    @Path("/{id}")
    public Optional<Patient> getPatient(@NotEmpty @PathParam("id") final String patientId) {
        return this.patients.stream().filter(p -> patientId.equals(p.getId())).findFirst();
    }
}
