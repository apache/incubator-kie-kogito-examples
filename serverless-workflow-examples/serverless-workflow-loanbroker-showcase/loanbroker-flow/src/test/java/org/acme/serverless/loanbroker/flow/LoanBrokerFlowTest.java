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
package org.acme.serverless.loanbroker.flow;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.MediaType;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(SinkMock.class)
@QuarkusTestResource(QuotesAggregatorMock.class)
@QuarkusTestResource(CreditBureauMock.class)
public class LoanBrokerFlowTest {
    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    // Injected by Quarkus
    WireMockServer sinkServer;
    WireMockServer aggregatorServer;
    WireMockServer creditBureauServer;

    @Test
    void verifyLoanBroker() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
        };
        // Push the quote
        final String workflowResponse = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body("{ \"workflowdata\": { \"SSN\": \"123-45-6789\", \"term\": 30, \"amount\": 500000 } }")
                .post("/loanbroker")
                .then()
                .statusCode(201).extract().body().asPrettyString();
        assertNotNull(workflowResponse);

        final Map<String, Object> map = mapper.readValue(workflowResponse, typeRef);
        final String workflowId = map.get("id").toString();

        // check if we've received the event from the callback event, requesting for the quotes
        await()
                .atMost(10, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(
                        () -> sinkServer.verify(1,
                                postRequestedFor(urlEqualTo("/"))
                                        .withHeader("Content-type", containing("cloudevents"))
                                        .withRequestBody(containing("500000"))));

        // The workflow should wait for the aggregator CE reply to resume the process.
        // Let's push it
        final CloudEvent cloudEvent = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType("kogito.serverless.loanbroker.aggregated.quotes.response")
                .withSource(URI.create("/kogito/serverless/loanbroker/aggregator"))
                .withDataContentType(MediaType.APPLICATION_JSON)
                // we use the workflow id as the correlation key
                .withExtension("kogitoprocrefid", workflowId)
                .withData(String.format(
                        "{ \"quoteCount\": 2, \"kogitoProcessInstanceId\": \"%s\", \"completitionDate\": \"2022-05-28\" }",
                        workflowId).getBytes())
                .build();

        RestAssured.given()
                .header("Content-Type", "application/cloudevents+json")
                .body(Objects.requireNonNull(EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE))
                        .serialize(cloudEvent))
                .when()
                .post("/")
                .then()
                .statusCode(202);

        // The workflow should finish and we should receive a message in the Sink
        // The produced event must have the quote of one of the banks returned by the
        // QuoteAggregatorMockService
        await()
                .atMost(10, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(
                        () -> sinkServer.verify(1,
                                postRequestedFor(urlEqualTo("/")).withRequestBody(
                                        containing("quotes"))));
    }

}
