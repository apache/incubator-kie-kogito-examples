package org.kie.kogito.examples;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@QuarkusIntegrationTest
class ConsumingEventsOverHttpIT {

    private static final String CHANNEL_NAME = "move";

    @BeforeAll
    static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testEventOverHttp() {
        String id = given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("workflowdata", Collections.emptyMap()))
                .post("/start")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/start/{id}", id)
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(generateCloudEvent(id))
                .post("/")
                .then()
                .statusCode(202);

        await()
                .atLeast(1, SECONDS)
                .atMost(30, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get("/start/{id}", id)
                        .then()
                        .statusCode(404));
    }

    private String generateCloudEvent(String id) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(JsonFormat.getCloudEventJacksonModule())
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        try {
            return objectMapper.writeValueAsString(CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create(""))
                    .withType(CHANNEL_NAME)
                    .withTime(OffsetDateTime.now())
                    .withExtension("kogitoprocrefid", id)
                    .withData(objectMapper.writeValueAsBytes(Collections.singletonMap(CHANNEL_NAME,
                            "This has been injected by the event")))
                    .build());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
