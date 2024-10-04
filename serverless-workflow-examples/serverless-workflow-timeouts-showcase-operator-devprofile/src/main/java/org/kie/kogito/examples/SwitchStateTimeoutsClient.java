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

import static org.kie.kogito.examples.SwitchStateTimeoutsClient.CONFIG_KEY;

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
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(configKey = CONFIG_KEY)
public interface SwitchStateTimeoutsClient extends WorkflowClient {

    /**
     * SW id that was configured in the switch_state_timeouts.yaml.
     */
    String ID = "switchstatetimeouts";
    String URI = "/" + ID;
    String CONFIG_KEY = "switch_state_timeouts";

    @POST
    @Path(URI)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response post(@Context HttpHeaders httpHeaders, @QueryParam("businessKey") @DefaultValue("") String businessKey, String input);

    @GET
    @Path(URI)
    @Produces(MediaType.APPLICATION_JSON)
    Response get();

}

