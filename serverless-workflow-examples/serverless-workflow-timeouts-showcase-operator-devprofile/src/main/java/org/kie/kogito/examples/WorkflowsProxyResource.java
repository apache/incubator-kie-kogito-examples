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

import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * The objective of this class is to provide a simple proxy for the invocations from the UI to the SWs, and avoid
 * the UI to have to deal SWs external addresses that are provided by minikube (or the corresponding kubernetes variant).
 * By doing this, the UI will always work independently of the kubernetes variant we use.
 * Finally, to do the formal invocation of the SWs, we use the quarkus-rest-client extension.
 * For more information on that extension see: <a href="https://quarkus.io/extensions/io.quarkus/quarkus-rest-client"></a>
 */
@ApplicationScoped
@Path("/")
public class WorkflowsProxyResource {

    /**
     * External path for accessing the Callback State Timeouts SW, never change.
     */
    private static final String CALLBACK_STATE_TIMEOUTS_URI = "callback_state_timeouts";
    /**
     * External path for accessing the Switch State Timeouts SW, never change.
     */
    private static final String SWITCH_STATE_TIMEOUTS_URI = "switch_state_timeouts";
    /**
     * External path for accessing the Event State Timeouts SW, never change.
     */
    private static final String EVENT_STATE_TIMEOUTS_URI = "event_state_timeouts";
    /**
     * External path for accessing the Workflow Timeouts SW, never change.
     */
    private static final String WORKFLOW_TIMEOUTS_URI = "workflow_timeouts";

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

    @POST
    @Path(CALLBACK_STATE_TIMEOUTS_URI)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCS(@Context HttpHeaders httpHeaders, @QueryParam("businessKey") @DefaultValue("") String businessKey, String input) {
        return callbackStateTimeouts.post(httpHeaders, businessKey, input);
    }

    @GET
    @Path(CALLBACK_STATE_TIMEOUTS_URI)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCS() {
        return callbackStateTimeouts.get();
    }

    @POST
    @Path(SWITCH_STATE_TIMEOUTS_URI)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postSS(@Context HttpHeaders httpHeaders, @QueryParam("businessKey") @DefaultValue("") String businessKey, String input) {
        return switchStateTimeoutsClient.post(httpHeaders, businessKey, input);
    }

    @GET
    @Path(SWITCH_STATE_TIMEOUTS_URI)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSS() {
        return switchStateTimeoutsClient.get();
    }

    @POST
    @Path(EVENT_STATE_TIMEOUTS_URI)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postES(@Context HttpHeaders httpHeaders, @QueryParam("businessKey") @DefaultValue("") String businessKey, String input) {
        return eventStateTimeoutsClient.post(httpHeaders, businessKey, input);
    }

    @GET
    @Path(EVENT_STATE_TIMEOUTS_URI)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getES() {
        return eventStateTimeoutsClient.get();
    }

    @POST
    @Path(WORKFLOW_TIMEOUTS_URI)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postWT(@Context HttpHeaders httpHeaders, @QueryParam("businessKey") @DefaultValue("") String businessKey, String input) {
        return workflowTimeoutsClient.post(httpHeaders, businessKey, input);
    }

    @GET
    @Path(WORKFLOW_TIMEOUTS_URI)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWT() {
        return workflowTimeoutsClient.get();
    }
}
