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
import java.util.Collections;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.process.Processes;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;

@ApplicationScoped
public class PublishService {

    @Inject
    Processes processes;

    KafkaTestClient kafkaClient;
    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    String kafkaBootstrapServers;

    @PostConstruct
    void setup() {
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    @PreDestroy
    void close() {
        kafkaClient.shutdown();
    }

    public void publishMove(JsonNode workflowData, KogitoProcessContext context) {
        kafkaClient.produce(generateCloudEvent(context.getProcessInstance().getStringId()), "move");
    }

    private String generateCloudEvent(String id) {

        try {
            return objectMapper.writeValueAsString(CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create(""))
                    .withType("move")
                    .withTime(OffsetDateTime.now())
                    .withExtension("kogitoprocrefid", id)
                    .withData(objectMapper.writeValueAsBytes(Collections.singletonMap("move", "This has been injected by the event")))
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }

    }

}
