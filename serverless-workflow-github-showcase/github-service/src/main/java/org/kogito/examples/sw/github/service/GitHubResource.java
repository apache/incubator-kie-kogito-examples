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
package org.kogito.examples.sw.github.service;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/repo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GitHubResource {

    @Inject
    GitHubWrapperService gitHubService;

    @POST
    @Path("/{user}/{name}/pr/{number}/labels")
    public Response addLabels(@PathParam("user") String user,
            @PathParam("name") String repoName,
            @PathParam("number") Integer prNumber,
            List<String> labels) {
        try {
            gitHubService.addLabels(user, repoName, prNumber, labels);
            return Response.ok().build();
        } catch (final Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{user}/{name}/pr/{number}/reviewers")
    public Response addReviewers(@PathParam("user") String user,
            @PathParam("name") String repoName,
            @PathParam("number") Integer prNumber,
            List<String> reviewers) {
        try {
            gitHubService.addReviewers(user, repoName, prNumber, reviewers);
            return Response.ok().build();
        } catch (final Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{user}/{name}/pr/{number}/files")
    public Response fetchFiles(@PathParam("user") String user,
            @PathParam("name") String repoName,
            @PathParam("number") Integer prNumber) {
        try {
            return Response.ok().entity(gitHubService.fetchChangedFilesPath(user, repoName, prNumber)).build();
        } catch (final Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
