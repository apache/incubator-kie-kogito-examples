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
package org.kie.kogito.examples;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

@Path("external-service")
public class ExternalServiceResource {

    private static final HashMap<String, Boolean> settings = new HashMap<>();

    private static final String CIRCUIT_BREAKER_ECHO = "circuitBreakerEcho";

    @POST
    @Path("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "adminOperation")
    public Response adminOperation(AdminRequest request) {
        settings.put(request.operation, request.enabled);
        return Response.ok().build();
    }

    @POST
    @Path("circuit-breaker")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "circuitBreakerEcho")
    public EchoResponse circuitBreakerEcho(String echo) {
        Boolean enabled = settings.get(CIRCUIT_BREAKER_ECHO);
        if (enabled != null && !enabled) {
            throw new WebApplicationException(CIRCUIT_BREAKER_ECHO + " is configured to fail", Response.Status.INTERNAL_SERVER_ERROR);
        }
        return new EchoResponse(UUID.randomUUID().toString(), echo, ZonedDateTime.now().toString());
    }
}
