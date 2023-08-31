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

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;

/**
 * Helper class used to facilitate testing using REST
 */
@Path("/account")
public class WorkflowResource {

    @Inject
    ObjectMapper objectMapper;

    @Channel("start")
    Emitter<String> emitter;

    @Inject
    EventsService eventsService;

    @POST
    @Path("/{userId}")
    public Response onEvent(@PathParam("userId") String userId) {
        String start = generateCloudEvent(userId, "newAccountEventType");
        emitter.send(start);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/{userId}")
    public Map<String, String> getProcessInstanceId(@PathParam("userId") String userId) {
        return Collections.singletonMap("processInstanceId", eventsService.getAccount(userId));
    }

    private String generateCloudEvent(String id, String type) {
        try {
            return objectMapper.writeValueAsString(CloudEventBuilder.v03()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create(""))
                    .withType(type)
                    .withTime(OffsetDateTime.now())
                    .withExtension("userid", id)
                    .withData(objectMapper.writeValueAsBytes(new Account("test@test.com", id)))
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
