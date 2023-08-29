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

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import jakarta.annotation.PostConstruct;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.avro.AvroCloudEventMarshaller;
import org.kie.kogito.event.avro.AvroIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import io.cloudevents.core.builder.CloudEventBuilder;

@Path("/newapplicant")
public class ApplicantResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicantResource.class);

    @Inject
    AvroIO avroIO;

    @Inject
    @Channel("out-applicants")
    Emitter<byte[]> newApplicantEmitter;

    private CloudEventMarshaller<byte[]> marshaller;

    @PostConstruct
    void init() {
        marshaller = new AvroCloudEventMarshaller(avroIO);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void submitApplicant(JsonNode newApplicant) {
        try {
            newApplicantEmitter.send(marshaller.marshall(CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withType("applicants")
                    .withSource(URI.create("http://localhost:8080"))
                    .withData(marshaller.cloudEventDataFactory().apply(newApplicant))
                    .build()));
        } catch (IOException e) {
            LOGGER.error("Unable to process cloud event", e);
            throw new InternalServerErrorException("Unable to write cloud event data", e);
        }
    }
}
