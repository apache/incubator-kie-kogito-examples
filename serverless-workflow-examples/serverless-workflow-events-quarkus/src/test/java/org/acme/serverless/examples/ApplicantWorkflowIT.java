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
package org.acme.serverless.examples;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSource;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.avro.AvroCloudEventMarshaller;
import org.kie.kogito.event.avro.AvroIO;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTypedTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class ApplicantWorkflowIT {

    private static final String DECISION_SSE_ENDPOINT = "http://localhost:%s/decisions/stream";
    
    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    @Test
    public void testApplicantProcess() throws Exception {
        Integer assignedPort = ConfigProvider.getConfig().getValue("quarkus.http.test-port", Integer.class);
        ObjectMapper mapper = ObjectMapperFactory.get();
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(String.format(DECISION_SSE_ENDPOINT, assignedPort));

        List<String> received = new CopyOnWriteArrayList<>();

        try (SseEventSource source = SseEventSource.target(target).reconnectingEvery(0, TimeUnit.MILLISECONDS).build()) {
            source.register(inboundSseEvent -> received.add(String.valueOf(inboundSseEvent.readData())));
            source.open();

            // Call the exposed domain endpoint
            given()
                    .body("{\"name\":\"Cristiano\",\"position\":\"iOS Engineer\",\"office\":\"Berlin\",\"salary\": 30000}")
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .when()
                    .post("/newapplicant")
                    .then()
                    .log().all()
                    .statusCode(204);

            await().atMost(1, MINUTES).until(() -> received.size() == 1);

            JsonNode approvedDecision = mapper.readTree(received.get(0));
            assertEquals("Approved", approvedDecision.get("data").get("decision").asText());

            AvroCloudEventMarshaller marshaller = new AvroCloudEventMarshaller( new AvroIO());
            new KafkaTypedTestClient<>(kafkaBootstrapServers, ByteArraySerializer.class, ByteArrayDeserializer.class).produce(marshaller.marshall(CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create("/from/test"))
                    //Start message event name in handle-travellers.bpmn
                    .withType("applicants")
                    .withTime(OffsetDateTime.now())
                    .withData(marshaller.cloudEventDataFactory().apply(mapper.readTree("{\"name\":\"Zanini\",\"position\":\"Android Engineer\",\"office\":\"Tokio\",\"salary\": 1000}")))
                    .build()), "applicants");
            await().atMost(1, MINUTES).until(() -> received.size() == 2);
            JsonNode deniedDecision = mapper.readTree(received.get(1));
            assertEquals("Denied", deniedDecision.get("data").get("decision").asText());
        } finally {
            if (client != null) {
                client.close();
            }
        }

    }
}
