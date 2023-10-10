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
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

@ApplicationScoped
@Path("/event")
public class CallbackResource {

    private static final Logger logger = LoggerFactory.getLogger(CallbackResource.class);


    @Inject
    ObjectMapper objectMapper;

    @Inject
    Vertx vertx;

    private WebClient webClient;

    @PostConstruct
    void init() {
        this.webClient = WebClient.create(vertx);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void wait(EventInput eventInput) throws JsonProcessingException {
        logger.info("About to generate event for {}",eventInput);
        CloudEventBuilder builder = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType("wait")
                .withTime(OffsetDateTime.now())
                .withExtension(CloudEventExtensionConstants.PROCESS_REFERENCE_ID, eventInput.getProcessInstanceId())
                .withData(objectMapper.writeValueAsBytes(Collections.singletonMap("message", "New Event")));

        webClient.postAbs(eventInput.getUri()).sendJson(builder.build()).toCompletionStage();
    }
}
