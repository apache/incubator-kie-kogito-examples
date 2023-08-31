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
package org.acme.loanbroker;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.ws.rs.core.MediaType;

import org.acme.loanbroker.domain.Credit;
import org.acme.loanbroker.domain.Quote;
import org.acme.loanbroker.domain.QuotesResponse;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class AppResourceTest {

    private static final LinkedBlockingDeque<String> QUOTES = new LinkedBlockingDeque<>();

    @Inject
    ObjectMapper mapper;

    @TestHTTPResource("/socket/quote/new")
    URI socketNewQuoteURI;

    @Test
    public void testCloudEventNotifyNewQuote() throws DeploymentException, IOException, InterruptedException {
        final QuotesResponse sentQuotes = new QuotesResponse();
        sentQuotes.setEventType("kogito.serverless.workflow.aggregated.quotes");
        sentQuotes.setLoanRequestId("1234");
        sentQuotes.setAmount(1000);
        sentQuotes.setTerm(10);
        sentQuotes.setCredit(new Credit(10, 10, "123-45-6789"));
        sentQuotes.setQuotes(Collections.singletonList(new Quote(12.234, "Bank1")));

        final CloudEvent ce = CloudEventBuilder.v1()
                .withId("123456")
                .withType("kogito.serverless.workflow.aggregated.quotes")
                .withSource(URI.create("/kogito/serverless/loanbroker/aggregator"))
                .withDataContentType(MediaType.APPLICATION_JSON)
                .withData(PojoCloudEventData.wrap(sentQuotes, mapper::writeValueAsBytes))
                .withExtension("kogitoprocinstanceid", "1234")
                .build();

        try (final Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, socketNewQuoteURI)) {
            // post the CE
            given()
                    .header("Content-Type", "application/cloudevents+json")
                    // see: https://cloudevents.github.io/sdk-java/json-jackson.html#using-the-json-event-format
                    .body(Objects.requireNonNull(EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE)).serialize(ce))
                    .when()
                    .post("/")
                    .then()
                    .statusCode(200);
            // wait for the message to be received, deserialize to the actual POJO
            final QuotesResponse receivedQuotes = mapper.readValue(QUOTES.poll(10, TimeUnit.SECONDS), QuotesResponse.class);
            // verify if the sent message is the same
            assertEquals(sentQuotes, receivedQuotes);
        }

    }

    @ClientEndpoint
    public static class Client {

        @OnMessage
        void message(String quotesResponse) {
            QUOTES.add(quotesResponse);
        }
    }

}
