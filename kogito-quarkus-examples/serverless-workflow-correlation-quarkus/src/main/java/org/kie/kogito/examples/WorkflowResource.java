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
package org.kie.kogito.examples;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import javax.ws.rs.PathParam;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;

@Path("/start1")
public class WorkflowResource {

    @Inject
    ObjectMapper objectMapper;

    @Channel("start")
    Emitter<String> emitter;

    @GET
    @Path("/{orderId}")
    public String onEvent(@PathParam("orderId") String orderId) {
        String start = generateCloudEvent(orderId, "startEventType");
        emitter.send(start);

        return start;
    }

    private String generateCloudEvent(String id, String input) {
        Map<String, Object> eventBody = new HashMap<>();
        eventBody.put("result", input + " and has been modified by the event publisher");
        eventBody.put("dummyEventVariable", "This will be discarded by the process");
        try {
            return objectMapper.writeValueAsString(CloudEventBuilder.v03()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create(""))
                    .withType(input)
                    .withTime(OffsetDateTime.now())
                    .withExtension("orderid", id)
                    .withData(objectMapper.writeValueAsBytes(eventBody))
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
