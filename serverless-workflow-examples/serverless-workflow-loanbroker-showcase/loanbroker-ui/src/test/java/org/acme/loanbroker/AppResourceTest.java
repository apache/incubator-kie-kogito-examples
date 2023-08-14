package org.acme.loanbroker;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import jakarta.inject.Inject;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.ws.rs.core.MediaType;

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
