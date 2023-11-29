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
package org.acme;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import io.quarkus.reactivemessaging.http.runtime.OutgoingHttpMetadata;

@Path("query-service")
@ApplicationScoped
public class QueryServiceResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryServiceResource.class);

    private static final String RESPONSE_EVENTS = "response_events";

    @Inject
    QueryRequestRepository repository;

    @Inject
    @Channel(RESPONSE_EVENTS)
    Emitter<String> eventsEmitter;

    @Inject
    ObjectMapper objectMapper;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<QueryRequest> get() {
        return repository.getAll();
    }

    @Path("sendQuery")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendQuery(QueryRequest request) {
        LOGGER.debug("Query request received: {}", request);
        repository.saveOrUpdate(request);
        return Response.ok("{}").build();
    }

    @Path("resolveQuery")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resolveQuery(ResolveRequest request) {
        String event = generateCloudEvent(request.getProcessInstanceId(), request.getQueryResponse());
        LOGGER.debug("Resolving query for processInstanceId:{}, event to send is: {}", request.getProcessInstanceId(), event);
        eventsEmitter.send(Message.of(event).addMetadata(new OutgoingHttpMetadata.Builder().addHeader("content-type", "application/cloudevents+json").build()));
        repository.delete(request.getProcessInstanceId());
        return Response.ok("{}").build();
    }

    public String generateCloudEvent(String processInstanceId, String queryResponse) {
        try {
            return objectMapper.writeValueAsString(CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create("query-service"))
                    .withType("query_response_events")
                    .withTime(OffsetDateTime.now())
                    .withExtension("kogitoprocrefid", processInstanceId)
                    .withData(JsonCloudEventData.wrap(objectMapper.createObjectNode().put("answer", queryResponse)))
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}