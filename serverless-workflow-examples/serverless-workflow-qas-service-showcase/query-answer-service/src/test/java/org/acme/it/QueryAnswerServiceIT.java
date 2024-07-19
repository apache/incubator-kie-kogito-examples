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
package org.acme.it;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.acme.QueryRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@QuarkusIntegrationTest
@QuarkusTestResource(WireMockQueryServiceResource.class)
@QuarkusTestResource(KafkaQuarkusTestResource.class)
class QueryAnswerServiceIT {

    private static final String QUERY = "THE FORMULATED QUERY";
    private static final String ANSWER = "THE RECEIVED ANSWER";
    private static final String QUERY_ANSWER_SERVICE_URL = "/qaservice";
    private static final String QUERY_ANSWER_SERVICE_GET_BY_ID_URL = QUERY_ANSWER_SERVICE_URL + "/{id}";

    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;
    private ObjectMapper objectMapper;

    private KafkaTestClient kafkaClient;

    @BeforeEach
    void setup() {
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(JsonFormat.getCloudEventJacksonModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void queryAnswerProcessCycle() throws Exception {
        // start a new process instance by sending a query and collect the process instance id.
        String processInput = "{\"query\": \"" + QUERY + "\"}";
        String processInstanceId = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(processInput)
                .post(QUERY_ANSWER_SERVICE_URL)
                .then()
                .statusCode(201)
                .extract()
                .path("id");
        assertThat(processInstanceId).isNotBlank();

        // double check that the process instance is there.
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(QUERY_ANSWER_SERVICE_GET_BY_ID_URL, processInstanceId)
                .then()
                .statusCode(200);

        // prepare and send the response to the created process via kafka
        String response = objectMapper.writeValueAsString(CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType("query_response_events")
                .withTime(OffsetDateTime.now())
                .withExtension("kogitoprocrefid", processInstanceId)
                .withData(JsonCloudEventData.wrap(objectMapper.createObjectNode().put("answer", ANSWER)))
                .build());

        kafkaClient.produce(response, "query_response_events");

        // give some time for the event to be processed and the process to finish.
        await()
                .atLeast(1, SECONDS)
                .atMost(120, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get(QUERY_ANSWER_SERVICE_GET_BY_ID_URL, processInstanceId)
                        .then()
                        .statusCode(404));

        JsonPath currentQueries = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/queries")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath();

        // verify the query answer was properly set by the process.
        List<QueryRecord> queryRecords = currentQueries.getList("", QueryRecord.class);
        assertThat(queryRecords).hasSize(1);
        QueryRecord queryRecord = queryRecords.get(0);
        assertThat(queryRecord.getQuery()).isEqualTo(QUERY);
        assertThat(queryRecord.getAnswer()).isEqualTo(ANSWER);
    }

    @AfterEach
    void cleanUp() {
        kafkaClient.shutdown();
    }

}
