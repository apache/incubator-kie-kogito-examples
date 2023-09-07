/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Helper resource with convenient operations to produce events for the serverless workflows that are waiting for
 * events to arrive. The UI relies on these invocations, and thus we keep it simple. Analogous to the
 * WorkflowsProxyResource this class relies on the quarkus-rest-client extension the to do the formal invocation and
 * sending of the events to the SWs.
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
    private static final String CALLBACK_EVENT_TYPE = "callback_event_type";
    /**
     * Event type expected by the event_state_timeouts sw to execute the associated actions.
     */
    private static final String EVENT1_EVENT_TYPE = "event1_event_type";
    /**
     * Event type expected by the event_state_timeouts sw to execute the associated actions.
     */
    private static final String EVENT2_EVENT_TYPE = "event2_event_type";
    /**
     * Event type expected by the workflow_timeouts sw to execute the associated actions.
     */
    private static final String WAKE_UP_EVENT_TYPE = "wake_up_event_type";

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @RestClient
    CallbackStateTimeoutsClient callbackStateTimeouts;

    @Inject
    @RestClient
    SwitchStateTimeoutsClient switchStateTimeoutsClient;

    @Inject
    @RestClient
    EventStateTimeoutsClient eventStateTimeoutsClient;

    @Inject
    @RestClient
    WorkflowTimeoutsClient workflowTimeoutsClient;

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
        String cloudEvent = generateCloudEvent(processInstanceId, CALLBACK_EVENT_TYPE, event);
        return produceResponse(callbackStateTimeouts.sendEvent(cloudEvent));
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
        return produceResponse(switchStateTimeoutsClient.sendEvent(cloudEvent));
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
        return produceResponse(switchStateTimeoutsClient.sendEvent(cloudEvent));
    }

    /**
     * Produce the event1 for an instance of the event-state-timeouts serverless workflow.
     *
     * @param processInstanceId Process instance id of the process that will receive the event.
     * @param event event to send.
     */
    @Path("produce-event-state-timeouts-event1/{processInstanceId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response produceEventStateTimeoutsEvent1(@PathParam("processInstanceId") String processInstanceId, Event event) {
        String cloudEvent = generateCloudEvent(processInstanceId, EVENT1_EVENT_TYPE, event);
        return produceResponse(eventStateTimeoutsClient.sendEvent(cloudEvent));
    }

    /**
     * Produce the event2 for an instance of the event-state-timeouts serverless workflow.
     *
     * @param processInstanceId Process instance id of the process that will receive the event.
     * @param event event to send.
     */
    @Path("produce-event-state-timeouts-event2/{processInstanceId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response produceEventStateTimeoutsEvent2(@PathParam("processInstanceId") String processInstanceId, Event event) {
        String cloudEvent = generateCloudEvent(processInstanceId, EVENT2_EVENT_TYPE, event);
        return produceResponse(eventStateTimeoutsClient.sendEvent(cloudEvent));
    }

    /**
     * Produce the wake-event for an instance of the workflow-timeouts serverless workflow.
     *
     * @param processInstanceId Process instance id of the process that will receive the event.
     * @param event event to send.
     */
    @Path("produce-workflow-timeouts-event/{processInstanceId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response produceWorkflowTimeoutsWakeUpEvent(@PathParam("processInstanceId") String processInstanceId, Event event) {
        String cloudEvent = generateCloudEvent(processInstanceId, WAKE_UP_EVENT_TYPE, event);
        return produceResponse(workflowTimeoutsClient.sendEvent(cloudEvent));
    }

    private static Response produceResponse(Response response) {
        if (response.getStatus() == 202) {
            //client side ajax expect code 200.
            return Response.ok().entity("{}").build();
        }
        return response;
    }

    private String generateCloudEvent(String processInstanceId, String eventType, Event event) {
        try {
            return objectMapper.writeValueAsString(CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create("events-producer"))
                    .withType(eventType)
                    .withTime(OffsetDateTime.now())
                    .withExtension("kogitoprocrefid", processInstanceId)
                    .withDataContentType(MediaType.APPLICATION_JSON)
                    .withData(JsonCloudEventData.wrap(objectMapper.createObjectNode().put("eventData", event.getEventData())))
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
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