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
package org.kogito.examples.sw.notification.service;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;

@Path("")
@ApplicationScoped
public class NotificationResource {

    @Inject
    ObjectMapper mapper;

    @Inject
    ProducerTemplate producerTemplate;

    @ConfigProperty(name = "org.kogito.examples.sw.notification.slack.channel")
    String channel;

    @ConfigProperty(name = "org.kogito.examples.sw.notification.slack.incoming")
    String webHookUrl;

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/plain")
    public Response createSlackMessage(String message) {
        producerTemplate.requestBody("slack:#" + channel + "?webhookUrl=" + webHookUrl, message);
        return Response
                .ok()
                .build();
    }

    @POST
    //Knative sends the message to the root path
    public Response createSlackMessage(CloudEvent message) throws IOException {
        producerTemplate.requestBody("slack:#" + channel + "?webhookUrl=" + webHookUrl, formatMessageFromCE(message));
        return Response
                .ok()
                .build();
    }

    private String formatMessageFromCE(CloudEvent message) throws IOException {
        final JsonNode jsonNode = mapper.readTree(message.getData().toBytes());
        return String.format("Heads Up! *%s* event on PR *#%s*, named *%s*", message.getType(), jsonNode.get("number").asText(), jsonNode.get("pull_request").get("title").asText());
    }
}
