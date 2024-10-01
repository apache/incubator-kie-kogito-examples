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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment.Strategy;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
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
public class EventsService {

    private static final Logger logger = LoggerFactory.getLogger(EventsService.class);

    @Inject
    ObjectMapper objectMapper;

    private Map<String, String> accounts = new ConcurrentHashMap<>();

    public void complete(JsonNode workflowData, KogitoProcessContext context) {
        logger.info("Complete Account Creation received. Workflow data {}, KogitoProcessInstanceId {} ", workflowData, context.getProcessInstance().getStringId());
    }

    @Incoming("validate")
    @Outgoing("validated")
    @Acknowledgment(Strategy.POST_PROCESSING)
    public String onEventValidate(Message<String> message) {
        Optional<CloudEvent> ce = CloudEventUtils.decode(message.getPayload());
        JsonCloudEventData cloudEventData = (JsonCloudEventData) ce.get().getData();
        logger.info("Validate Account received. Workflow data {}", cloudEventData);
        String userId = ce.get().getExtension("userid").toString();

        //just for testing
        accounts.put(userId, ce.get().getExtension("kogitoprocinstanceid").toString());

        return generateCloudEvent(userId, "validatedAccountEmail", null);
    }

    @Incoming("activate")
    @Outgoing("activated")
    @Acknowledgment(Strategy.POST_PROCESSING)
    public String onEventActivate(Message<String> message) {
        Optional<CloudEvent> ce = CloudEventUtils.decode(message.getPayload());
        JsonCloudEventData cloudEventData = (JsonCloudEventData) ce.get().getData();
        logger.info("Activate Account received. Workflow data {}", cloudEventData);
        return generateCloudEvent(ce.get().getExtension("userid").toString(), "activatedAccount", null);
    }

    private String generateCloudEvent(String id, String type, Object data) {
        try {
            return objectMapper.writeValueAsString(CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create(""))
                    .withType(type)
                    .withTime(OffsetDateTime.now())
                    .withExtension("userid", id)
                    .withData(objectMapper.writeValueAsBytes(data))
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public final String getAccount(String userId) {
        return accounts.get(userId);
    }
}
