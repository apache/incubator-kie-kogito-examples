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
package org.acme.serverless.loanbroker.aggregator;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.acme.serverless.loanbroker.aggregator.model.BankQuote;
import org.apache.camel.Exchange;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.acme.serverless.loanbroker.aggregator.IntegrationConstants.KOGITO_FLOW_ID_HEADER;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@QuarkusTest
@QuarkusTestResource(SinkMockTestResource.class)
public class QuotesAggregatorRouteTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Inject
    ObjectMapper objectMapper;

    @Inject
    QuotesRepositoryProcessor quotesRepository;

    @InjectWithSinkMock
    WireMockServer wireMockServer;

    @AfterEach
    void cleanUpRepository() {
        if (quotesRepository != null) {
            quotesRepository.clear();
        }
    }

    @Test
    void verifyOneMessageAggregated() {
        this.postMessageAndExpectSuccess(
                new BankQuote("BankPremium", 4.655600086643112), "123");

        await()
                .atMost(10, TimeUnit.SECONDS)
                .with().pollInterval(3, TimeUnit.SECONDS)
                .untilAsserted(() -> this.getQuotesAndAssert(1, "123"));

        assertAggregatorReply(1);
    }

    /**
     * @throws InterruptedException
     * @throws JsonProcessingException
     */
    @Test
    void verifyManyQuotesSingleInstanceMessageAggregated() {
        this.postMessageAndExpectSuccess(
                new BankQuote("BankPremium", 4.655600086643112), "123");
        this.postMessageAndExpectSuccess(
                new BankQuote("BankStar", 5.4342645), "123");

        await()
                .atMost(10, TimeUnit.SECONDS)
                .with().pollInterval(3, TimeUnit.SECONDS)
                .untilAsserted(() -> this.getQuotesAndAssert(2, "123"));

        assertAggregatorReply(1);
    }

    @Test
    void verifyManyQuotesManyInstancesMessageAggregated() {
        this.postMessageAndExpectSuccess(
                new BankQuote("BankPremium", 4.655600086643112), "123");
        this.postMessageAndExpectSuccess(
                new BankQuote("BankPremium", 5.4342645), "456");

        await()
                .atMost(10, TimeUnit.SECONDS)
                .with().pollInterval(3, TimeUnit.SECONDS)
                .untilAsserted(() -> this.getQuotesAndAssert(1, "123"));
        await()
                .atMost(10, TimeUnit.SECONDS)
                .with().pollInterval(3, TimeUnit.SECONDS)
                .untilAsserted(() -> this.getQuotesAndAssert(1, "456"));

        assertAggregatorReply(2);
    }

    private void postMessageAndExpectSuccess(final BankQuote bankQuote, final String workflowInstanceId) {

        final CloudEvent ce = CloudEventBuilder.v1()
                .withId("123456")
                .withType("kogito.serverless.loanbroker.bank.offer")
                .withSource(URI.create("/local/tests"))
                .withDataContentType(MediaType.APPLICATION_JSON)
                .withData(PojoCloudEventData.wrap(bankQuote, objectMapper::writeValueAsBytes))
                .withExtension(KOGITO_FLOW_ID_HEADER, workflowInstanceId)
                .build();

        RestAssured.given()
                .header("Content-Type", "application/cloudevents+json")
                // see:
                // https://cloudevents.github.io/sdk-java/json-jackson.html#using-the-json-event-format
                .body(EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE)
                        .serialize(ce))
                .when()
                .post("/")
                .then()
                .statusCode(200);
    }

    private void getQuotesAndAssert(final int quotesCount, final String workflowInstanceId) {
        RestAssured.given()
                .get("/quotes/" + workflowInstanceId)
                .then()
                .statusCode(200)
                .and()
                .body("size()", Is.is(quotesCount));
    }

    private void assertAggregatorReply(final int count) {
        wireMockServer.verify(count, postRequestedFor(urlEqualTo("/"))
                .withHeader(Exchange.CONTENT_TYPE, equalTo("application/cloudevents+json"))
                .withRequestBody(containing("quoteCount")));
        wireMockServer.resetRequests();
    }
}
