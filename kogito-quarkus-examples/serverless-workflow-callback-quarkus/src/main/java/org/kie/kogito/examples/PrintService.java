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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment.Strategy;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;

@ApplicationScoped
public class PrintService {

    private static final Logger logger = LoggerFactory.getLogger(PrintService.class);

    @Inject
    ObjectMapper objectMapper;

    public void printKogitoProcessId(JsonNode workflowData, KogitoProcessContext context) {
        logger.info("Workflow data {}, KogitoProcessInstanceId {} ", workflowData, context.getProcessInstance().getStringId());
    }

    @Incoming("in-resume")
    @Outgoing("out-wait")
    @Acknowledgment(Strategy.POST_PROCESSING)
    public String onEvent(Message<String> message) {
        Optional<CloudEvent> ce = CloudEventUtils.decode(message.getPayload());
        JsonCloudEventData cloudEventData = (JsonCloudEventData) ce.get().getData();
        return generateCloudEvent(ce.get().getExtension(CloudEventExtensionConstants.PROCESS_INSTANCE_ID).toString(), cloudEventData.getNode().get("move").asText());
    }

    private String generateCloudEvent(String id, String input) {
        Map<String, Object> eventBody = new HashMap<>();
        eventBody.put("result", input + " and has been modified by the event publisher");
        eventBody.put("dummyEventVariable", "This will be discarded by the process");
        try {
            return objectMapper.writeValueAsString(CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create(""))
                    .withType("wait")
                    .withTime(OffsetDateTime.now())
                    .withExtension(CloudEventExtensionConstants.PROCESS_REFERENCE_ID, id)
                    .withData(objectMapper.writeValueAsBytes(eventBody))
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }

    }

}
