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
package org.acme.sw.onboarding.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

import org.acme.sw.onboarding.model.Assignment;
import org.acme.sw.onboarding.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/onboarding/patient")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PatientResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientResource.class);
    /*
    In a real world we would have a Database.
    See: https://quarkus.io/guides/hibernate-orm-panache
     */
    private final List<Patient> patients;

    public PatientResource() {
        this.patients = new ArrayList<>();
    }

    @POST
    public Response storeNewPatient(@NotNull final Assignment assignment) {
        LOGGER.debug("Received patients to store in the internal in memory database: {}", assignment);
        if (assignment.getPatients().isEmpty()) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new Error("Received an empty Patient list. At least one patient is expected."))
                    .build();
        }
        assignment.getPatients().forEach(p -> {
            p.setId(UUID.randomUUID().toString());
        });
        patients.addAll(assignment.getPatients());
        LOGGER.debug("All patients have been stored in the internal memory: {}", assignment);
        return Response.ok(assignment).build();
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
