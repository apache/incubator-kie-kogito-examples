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
package org.acme.messaging;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.acme.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.incubation.common.MapDataContext;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(value = KafkaQuarkusTestResource.class, initArgs = {
        @ResourceArg(name = KafkaQuarkusTestResource.KOGITO_KAFKA_TOPICS, value = "hello,hello-response")
})
public class HelloProcessWithMessagingIT {

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    String kafkaBootstrapServers;

    @Inject
    ObjectMapper objectMapper;

    private KafkaTestClient kafkaClient;

    @BeforeEach
    public void setup() {
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
    }

    @AfterEach
    public void tearDown() {
        if (kafkaClient != null) {
            kafkaClient.shutdown();
        }
    }

    @Test
    void testHelloProcessMessaging() throws Exception {
        //publish message to start the process
        User user = new User("Marty", "McFly");
        kafkaClient.produce(objectMapper.writeValueAsString(user), "hello");

        //subscribe to receive the response from process
        CompletableFuture<MapDataContext> futureResponse = new CompletableFuture<>();
        kafkaClient.consume("hello-response", event -> {
            try {
                MapDataContext context = objectMapper.readValue(event, MapDataContext.class);
                futureResponse.complete(context);
            } catch (JsonProcessingException e) {
                futureResponse.completeExceptionally(e);
            }
        });
        MapDataContext response = futureResponse.get(60, TimeUnit.SECONDS);
        assertThat(response.get("greetings")).isEqualTo("Hello, Marty McFly!");
    }
}
