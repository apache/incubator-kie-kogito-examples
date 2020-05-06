package org.kogito.serverless.examples;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(KafkaResource.class)
public class ApplicantWorkflowTest {
    private static final String DECISION_SSE_ENDPOINT = "http://localhost:8081/decisions/stream";

    @Test
    public void testApplicantProcess() throws Exception {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(DECISION_SSE_ENDPOINT);

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
        Assert.assertEquals("Approved", decisionObj.get("data").get("decision").asText());
    }

}
