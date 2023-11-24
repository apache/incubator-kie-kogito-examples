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
package org.acme.travel;

import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@QuarkusTest
public class CloudEventListenerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventListenerTest.class);
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
    void checkStartNewProcessInstanceWithCEForAmericans() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Traveller traveller = new Traveller();
        traveller.setFirstName("Jane");
        traveller.setLastName("Doe");
        traveller.setEmail("jane.doe@example.com");
        traveller.setNationality("American");

        given()
                .header("ce-specversion", "1.0")
                .header("ce-id", "000")
                .header("ce-source", "/from/test")
                .header("ce-type", "travellers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(traveller)).post("/").then().statusCode(202);
    }

    @Test
    void checkStartNewProcessInstanceWithCE() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Traveller traveller = new Traveller();
        traveller.setFirstName("Jane");
        traveller.setLastName("Doe");
        traveller.setEmail("jane.doe@example.com");
        traveller.setNationality("Polish");

        given()
                .header("ce-specversion", "1.0")
                .header("ce-id", "000")
                .header("ce-source", "/from/test")
                .header("ce-type", "travellers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(traveller)).post("/").then().statusCode(202);

        LOGGER.info("Waiting at most 2 seconds to receive the produced message");
        await().atMost(2, SECONDS).untilAsserted(() -> sink.verify(1, postRequestedFor(urlEqualTo("/"))
                .withHeader("Content-Type", containing("application/cloudevents+json"))
                .withRequestBody(containing("jane.doe@example.com"))
                .withRequestBody(containing("\"source\":\"/process/Travelers\""))));
    }

    @Test
    void checkStartNewProcessInstanceWithSourceField() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Traveller traveller = new Traveller();
        traveller.setFirstName("Jane");
        traveller.setLastName("Doe");
        traveller.setEmail("jane.doe2@example.com");
        traveller.setNationality("German");

        given()
                .header("ce-specversion", "1.0")
                .header("ce-id", "000")
                .header("ce-source", "travellers")
                .header("ce-type", "whatevertype")
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(traveller)).post("/").then().statusCode(202);

        LOGGER.info("Waiting at most 2 seconds to receive the produced message");
        await().atMost(2, SECONDS).untilAsserted(() -> sink.verify(1, postRequestedFor(urlEqualTo("/"))
                .withHeader("Content-Type", containing("application/cloudevents+json"))
                .withRequestBody(containing("jane.doe2@example.com"))
                .withRequestBody(containing("\"source\":\"/process/Travelers\""))));
    }
}
