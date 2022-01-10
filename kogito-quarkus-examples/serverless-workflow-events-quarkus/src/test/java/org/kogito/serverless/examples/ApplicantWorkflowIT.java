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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSource;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource.KOGITO_KAFKA_TOPICS;

@QuarkusIntegrationTest
@QuarkusTestResource(value = KafkaQuarkusTestResource.class, initArgs = { @ResourceArg(name = KOGITO_KAFKA_TOPICS, value = "applicants,decisions") })
public class ApplicantWorkflowIT {

    private static final String DECISION_SSE_ENDPOINT = "http://localhost:%s/decisions/stream";

    @Test
    public void testApplicantProcess() throws Exception {
        Integer assignedPort = ConfigProvider.getConfig().getValue("quarkus.http.test-port", Integer.class);
        System.out.println("assignedPort = " + assignedPort);
        ObjectMapper mapper = new ObjectMapper();
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(String.format(DECISION_SSE_ENDPOINT, assignedPort));

        List<String> received = new CopyOnWriteArrayList<>();

        try (SseEventSource source = SseEventSource.target(target).reconnectingEvery(0, TimeUnit.MILLISECONDS).build()) {
            source.register(inboundSseEvent -> {
                System.out.println("\n\n\n>>> inboundSseEvent = " + inboundSseEvent);
                received.add(String.valueOf(inboundSseEvent.readData()));
            }, t -> {
                System.out.println(">>>>onerror");
                t.printStackTrace();
            });
            source.open();
            System.out.println("source.isOpen() = " + source.isOpen());

            // Call the exposed domain endpoint
            given()
                    .body("{\"name\":\"Cristiano\",\"position\":\"iOS Engineer\",\"office\":\"Berlin\",\"salary\": 30000}")
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .when()
                    .post("/newapplicant")
                    .then()
                    .log().all()
                    .statusCode(204);

            await().atMost(1, MINUTES).until(() -> {
                System.out.println("received = " + received.size());
                return received.size() == 1;
            });

            JsonNode approvedDecision = mapper.readTree(received.get(0));
            assertEquals("Approved", approvedDecision.get("data").get("decision").asText());

            // Produce an HTTP CE Event in the root path
            given()
                    .header("ce-specversion", "1.0")
                    .header("ce-id", "000")
                    .header("ce-source", "/from/test")
                    .header("ce-type", "applicants")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"name\":\"Zanini\",\"position\":\"Android Engineer\",\"office\":\"Tokio\",\"salary\": 1000}")
                    .post("/")
                    .then()
                    .statusCode(200);
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
