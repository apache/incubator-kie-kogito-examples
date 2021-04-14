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
package org.kie.kogito.examples.sw.orders.processing;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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

@QuarkusTest
public class VerifyWorkflowExecutionIT {

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
    void processDomesticOrderUnderFraudEval() throws JsonProcessingException, InterruptedException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setDescription("iPhone 12");
        order.setTotal(1001);
        order.setCountry("US");

        given()
                .header("ce-specversion", "1.0")
                .header("ce-id", order.getId())
                .header("ce-source", "/from/test")
                .header("ce-type", "orderEvent")
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(order)).post("/").then().statusCode(200);
        // WireShark does not support assert until timeout
        TimeUnit.SECONDS.sleep(1);
        sink.verify(2, postRequestedFor(urlEqualTo("/")).withRequestBody(containing(order.getId())));
    }

    @Test
    void processDomesticOrderNoFraudEval() throws JsonProcessingException, InterruptedException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setDescription("iPhone 12");
        order.setTotal(1000);
        order.setCountry("US");

        given()
                .header("ce-specversion", "1.0")
                .header("ce-id", order.getId())
                .header("ce-source", "/from/test")
                .header("ce-type", "orderEvent")
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(order)).post("/").then().statusCode(200);
        // WireShark does not support assert until timeout
        TimeUnit.SECONDS.sleep(1);
        sink.verify(1, postRequestedFor(urlEqualTo("/")).withRequestBody(containing(order.getId())));
    }

    @Test
    void processInternationalOrderNoFraudEval() throws JsonProcessingException, InterruptedException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setDescription("iPhone 7");
        order.setTotal(500);
        order.setCountry("Brazil");

        given()
                .header("ce-specversion", "1.0")
                .header("ce-id", order.getId())
                .header("ce-source", "/from/test")
                .header("ce-type", "orderEvent")
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(order)).post("/").then().statusCode(200);
        // WireShark does not support assert until timeout
        TimeUnit.SECONDS.sleep(1);
        sink.verify(1, postRequestedFor(urlEqualTo("/")).withRequestBody(containing(order.getId())));
    }
}
