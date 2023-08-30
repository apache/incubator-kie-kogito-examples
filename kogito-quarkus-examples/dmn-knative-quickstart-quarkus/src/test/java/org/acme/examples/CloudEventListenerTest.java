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
package org.acme.examples;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@QuarkusTest
public class CloudEventListenerTest {

    private static final String KOGITO_MODEL_NAME = "Traffic Violation";
    private static final String KOGITO_MODEL_NAMESPACE = "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF";

    private static WireMockServer sink;

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

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
    void checkDecision() throws JsonProcessingException {
        final Map<String, Object> decisionInput = Map.of(
                "Driver", Map.of("Age", 35, "Points", 3),
                "Violation", Map.of("Type", "speed", "Actual Speed", 115, "Speed Limit", 100));

        given()
                .header("ce-specversion", "1.0")
                .header("ce-id", "000")
                .header("ce-source", "/from/test")
                .header("ce-type", "DecisionRequest")
                .header("ce-kogitodmnmodelname", KOGITO_MODEL_NAME)
                .header("ce-kogitodmnmodelnamespace", KOGITO_MODEL_NAMESPACE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().writeValueAsString(decisionInput)).post("/").then().statusCode(202);

        await()
                .atLeast(2, SECONDS)
                .atMost(10, SECONDS)
                .with().pollInterval(2, SECONDS)
                .untilAsserted(() -> sink.verify(1, postRequestedFor(urlEqualTo("/"))
                        .withRequestBody(matchingJsonPath("$.specversion", new EqualToPattern("1.0")))
                        .withRequestBody(matchingJsonPath("$.source", new EqualToPattern(KOGITO_MODEL_NAME.replace(" ", "+"))))
                        .withRequestBody(matchingJsonPath("$.type", new EqualToPattern("DecisionResponse")))
                        .withRequestBody(matchingJsonPath("$.kogitodmnmodelnamespace", new EqualToPattern(KOGITO_MODEL_NAMESPACE)))
                        .withRequestBody(matchingJsonPath("$.kogitodmnmodelname", new EqualToPattern(KOGITO_MODEL_NAME)))));
    }
}
