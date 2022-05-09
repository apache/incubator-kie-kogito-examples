/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.acme.newsletter.subscription.flow;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.acme.newsletter.subscription.service.Subscription;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.v1.CloudEventBuilder;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.acme.newsletter.subscription.flow.SubscriptionConstants.EMAIL;
import static org.acme.newsletter.subscription.flow.SubscriptionConstants.newSubscription;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@QuarkusTest
@QuarkusTestResource(SubscriptionServiceMock.class)
public class SubscriptionFlowTest {

    // Mocking our Knative broker, the new.subscriber event should be broadcast to it
    private static WireMockServer sink;

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /**
     * Starts the "sink" server, which is is the endpoint that will receive our produced events
     */
    @BeforeAll
    public static void startSink() {
        sink = new WireMockServer(options().port(8181));
        sink.start();
        sink.stubFor(post("/").willReturn(aResponse().withBody("ok").withStatus(200)));
    }

    @AfterAll
    public static void stopSink() {
        if (sink != null) {
            sink.stop();
        }
    }

    @Test
    void verifySubscription() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
        };
        // ask to subscribe
        final String bodyAfterSubs = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body("{\"workflowdata\": {\"email\": \"" + EMAIL + "\"}}")
                .post("/subscription_flow")
                .then()
                .statusCode(201).extract().body().asPrettyString();

        assertThat(bodyAfterSubs).isNotEmpty();

        // we need the workflow instance id to confirm our subscription
        final Map<String, Object> map = mapper.readValue(bodyAfterSubs, typeRef);
        final String workflowId = map.get("id").toString();
        final Subscription subscription = newSubscription();
        subscription.setId(workflowId);

        // now we send the CE with our confirmation to end the subscription flow
        final CloudEvent confirmationEvent = new CloudEventBuilder()
                .newBuilder()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create("/from/test"))
                .withType("confirm.subscription") // see the event type in the workflow file, the workflow engine will correlate it to our callback state :)
                .withExtension("kogitoprocrefid", workflowId) // required to correlate this event with the running workflow instance
                .build();
        given()
                .contentType(ContentType.JSON)
                .header("ce-specversion", confirmationEvent.getSpecVersion())
                .header("ce-id", confirmationEvent.getId())
                .header("ce-source", confirmationEvent.getSource().toString())
                .header("ce-type", confirmationEvent.getType())
                .header("ce-kogitoprocrefid", confirmationEvent.getExtension("kogitoprocrefid"))
                .body(mapper.writeValueAsString(subscription))
                .post("/")// the root path means we are listening for CEs for Knative Eventing integration
                .then()
                .statusCode(200);

        // the workflow should emit a new event indicating that the subscription was successful!
        await()
                .atMost(10, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> sink.verify(1, postRequestedFor(urlEqualTo("/")).withRequestBody(containing(EMAIL))));
    }

}
