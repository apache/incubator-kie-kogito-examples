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

import java.util.Collections;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.sw.onboarding.model.Assignment;
import org.acme.sw.onboarding.model.Patient;
import org.acme.sw.onboarding.services.AssignmentUnitClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/onboarding/assignment")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssignmentResource {

    @RestClient
    @Inject
    AssignmentUnitClient assignmentUnitClient;

    @POST
    public Response assignPatientToDoctor(@NotNull final Patient patient) {
        final Assignment assignment = new Assignment();
        assignment.setPatients(Collections.singletonList(patient));
        return Response.ok(assignmentUnitClient.assignDoctorFirst(assignment)).build();
    }
}
