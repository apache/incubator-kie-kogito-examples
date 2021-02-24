/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kogito.serverless.examples;

import java.net.URI;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

@Path("/newapplicant")
public class ApplicantResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicantResource.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    @Channel("out-applicants")
    Emitter<String> newApplicantEmitter;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void submitApplicant(JsonNode newApplicant) {
        try {
            CloudEvent applicantEvent = CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withType("applicants")
                    .withSource(URI.create("http://localhost:8080"))
                    .withData(mapper.writeValueAsString(newApplicant).getBytes())
                    .build();
            newApplicantEmitter.send(mapper.writeValueAsString(applicantEvent));
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to process cloud event", e);
            throw new InternalServerErrorException("Unable to write cloud event data", e);
        }
    }
}
