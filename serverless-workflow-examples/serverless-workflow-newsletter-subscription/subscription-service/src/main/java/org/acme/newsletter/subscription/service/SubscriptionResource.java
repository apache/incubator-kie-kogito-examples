/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.acme.newsletter.subscription.service;

import java.io.Serializable;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

@ApplicationScoped
@Path("/subscription")
@RegisterForReflection
public class SubscriptionResource {

    @Inject
    SubscriptionService service;

    @PUT
    @Path("/confirm")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponseSchema(value = Subscription.class, responseDescription = "success", responseCode = "200")
    public Response confirm(Subscription subscription) {
        if (subscription.hasId()) {
            try {
                return Response.ok(service.confirm(subscription)).build();
            } catch (SubscriptionException ex) {
                // TODO: add an error
                return Response.status(Response.Status.BAD_REQUEST).entity(null).build();
            }
        }
        // TODO: add an error
        return Response.status(Response.Status.BAD_REQUEST).entity(null).build();
    }

    @GET
    @Path("/verify")
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponseSchema(value = EmailVerificationReply.class, responseDescription = "success", responseCode = "200")
    public Response verify(@QueryParam("email") String email) {
        if (service.checkEmail(email)) {
            return Response.ok(new EmailVerificationReply(email, true)).build();
        }
        return Response.ok(new EmailVerificationReply(email, false)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponseSchema(value = Subscription.class, responseDescription = "success", responseCode = "200")
    public Response subscribe(Subscription subscription) {
        if (subscription.hasId()) {
            return Response.ok(service.subscribe(subscription)).build();
        }
        // TODO: add an error
        return Response.status(Response.Status.BAD_REQUEST).entity(null).build();
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponseSchema(value = Subscription.class, responseDescription = "success", responseCode = "200")
    public Response delete(@PathParam("id") String id) {
        service.delete(id);
        return Response.status(Response.Status.OK).entity("{}").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponseSchema(value = Subscription.class, responseDescription = "success", responseCode = "200")
    public Response fetch(@QueryParam("email") String email) {
        final Optional<Subscription> subscription = service.fetch(email);
        if (subscription.isPresent()) {
            return Response.ok(subscription.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("pending")
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponse(responseCode = "200", description = "success", content = {
            @Content(schema = @Schema(allOf = Subscription.class))
    })
    public Response fetchAllNotVerified() {
        return Response.ok(this.service.list(false)).build();
    }

    @GET
    @Path("verified")
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponse(responseCode = "200", description = "success", content = {
            @Content(schema = @Schema(allOf = Subscription.class))
    })
    public Response fetchAllVerified() {
        return Response.ok(this.service.list(true)).build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @RegisterForReflection
    public static class EmailVerificationReply implements Serializable {
        private final String email;
        private boolean emailExists;

        @JsonCreator
        public EmailVerificationReply(@JsonProperty("email") final String email) {
            this.email = email;
        }

        public EmailVerificationReply(final String email, final boolean emailExists) {
            this(email);
            this.emailExists = emailExists;
        }

        public String getEmail() {
            return email;
        }

        public boolean isEmailExists() {
            return emailExists;
        }

        public void setEmailExists(boolean emailExists) {
            this.emailExists = emailExists;
        }
    }
}
