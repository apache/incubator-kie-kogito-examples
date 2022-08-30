/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import io.quarkus.reactivemessaging.http.runtime.OutgoingHttpMetadata;

/**
 * Helper resource with convenient operations to produce events for the serverless workflows that are waiting for
 * events to arrive.
 */
@Path("events-producer")
@ApplicationScoped
public class EventsProducerResource {

    /**
     * Event type expected by the switch_state_timeouts sw to approve a visa.
     */
    private static final String VISA_APPROVED_EVENT_TYPE = "visa_approved_event_type";
    /**
     * Event type expected by the switch_state_timeouts sw to deny a visa.
     */
    private static final String VISA_DENIED_EVENT_TYPE = "visa_denied_event_type";
    /**
     * Event type expected by the callback_state_timeouts sw to receive the callback results from the callback function.
     */
    private static final String CALLBACK_STATE_EVENT_TYPE = "callback_state_event_type";

    /**
     * Outgoing channel for the response events sent to the processes.
     */
    private static final String RESPONSE_EVENTS = "response_events";

    @Inject
    @Channel(RESPONSE_EVENTS)
    Emitter<String> eventsEmitter;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Produce a callback event for an instance of the callback-state-timeouts serverless workflow.
     * 
     * @param processInstanceId Process instance id of the process that will receive the event.
     * @param event event to send.
     */
    @Path("produce-callback-state-timeouts-event/{processInstanceId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response produceCallbackStateTimeoutsEvent(@PathParam("processInstanceId") String processInstanceId, Event event) {
        String cloudEvent = generateCloudEvent(processInstanceId, CALLBACK_STATE_EVENT_TYPE, event);
        emitEvent(cloudEvent);
        return Response.ok("{}").build();
    }

    /**
     * Produce a visa approval event for an instance of the switch-state-timeouts serverless workflow.
     *
     * @param processInstanceId Process instance id of the process that will receive the event.
     * @param event event to send.
     */
    @Path("produce-switch-state-timeouts-visa-approved-event/{processInstanceId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response produceSwitchStateTimeoutsVistaApprovedEvent(@PathParam("processInstanceId") String processInstanceId, Event event) {
        String cloudEvent = generateCloudEvent(processInstanceId, VISA_APPROVED_EVENT_TYPE, event);
        emitEvent(cloudEvent);
        return Response.ok("{}").build();
    }

    /**
     * Produce a visa denial event for an instance of the switch-state-timeouts serverless workflow.
     *
     * @param processInstanceId Process instance id of the process that will receive the event.
     * @param event event to send.
     */
    @Path("produce-switch-state-timeouts-visa-denied-event/{processInstanceId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response produceSwitchStateTimeoutsVistaDeniedEvent(@PathParam("processInstanceId") String processInstanceId, Event event) {
        String cloudEvent = generateCloudEvent(processInstanceId, VISA_DENIED_EVENT_TYPE, event);
        emitEvent(cloudEvent);
        return Response.ok("{}").build();
    }

    private String generateCloudEvent(String processInstanceId, String eventType, Event event) {
        try {
            return objectMapper.writeValueAsString(CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create("events-producer"))
                    .withType(eventType)
                    .withTime(OffsetDateTime.now())
                    .withExtension("kogitoprocrefid", processInstanceId)
                    .withData(JsonCloudEventData.wrap(objectMapper.createObjectNode().put("eventData", event.getEventData())))
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void emitEvent(String cloudEvent) {
        eventsEmitter.send(Message.of(cloudEvent).addMetadata(new OutgoingHttpMetadata.Builder().addHeader("content-type", "application/cloudevents+json").build()));
    }

    public static class Event {
        private String eventData;

        public Event() {
        }

        public Event(String eventData) {
            this.eventData = eventData;
        }

        public String getEventData() {
            return eventData;
        }

        public void setEventData(String eventData) {
            this.eventData = eventData;
        }
    }
}