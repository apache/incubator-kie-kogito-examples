/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kogito.serverless.examples;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSource;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class ApplicantWorkflowTest {

    private static final String DECISION_SSE_ENDPOINT = "http://localhost:%s/decisions/stream";

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer assignedPort;

    @Test
    public void testApplicantProcess() throws Exception {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(String.format(DECISION_SSE_ENDPOINT, assignedPort));

        List<String> received = new CopyOnWriteArrayList<>();

        SseEventSource source = SseEventSource.target(target).build();
        source.register(inboundSseEvent -> received.add(String.valueOf(inboundSseEvent.readData())));
        source.open();

        given()
                .body("{\"name\":\"Cristiano\",\"position\":\"iOS Engineer\",\"office\":\"Berlin\",\"salary\":\"20000\"}")
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/newapplicant")
                .then()
                .statusCode(204);
        await().atMost(10000, MILLISECONDS).until(() -> received.size() == 1);
        source.close();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode decisionObj = mapper.readTree(received.get(0));
        assertEquals("Approved", decisionObj.get("data").get("decision").asText());
    }

}
