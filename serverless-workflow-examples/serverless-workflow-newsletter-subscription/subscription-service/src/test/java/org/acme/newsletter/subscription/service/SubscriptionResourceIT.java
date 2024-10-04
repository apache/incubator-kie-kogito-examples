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
package org.acme.newsletter.subscription.service;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
public class SubscriptionResourceIT {

    /**
     * You can't confirm if there's no subscription defined
     */
    @Test
    void verifyConfirmationNotFound() throws JsonProcessingException {
        final String email = "yennefer@witch.io";
        final String id = UUID.randomUUID().toString();
        final ObjectMapper objectMapper = new ObjectMapper();
        final Subscription subscription = new Subscription(email);
        subscription.setId(id);

        final String body = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(objectMapper.writeValueAsString(subscription))
                .put("/subscription/confirm")
                .then()
                .statusCode(400).extract().body().asString();

        assertThat(body).isEmpty();
    }

    /**
     * Happy path
     */
    @Test
    void verifySubscriptionCycle() throws JsonProcessingException {
        final String email = "yennefer@witch.io";
        final String id = UUID.randomUUID().toString();
        final ObjectMapper objectMapper = new ObjectMapper();
        final Subscription subscription = new Subscription(email);
        subscription.setId(id);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(objectMapper.writeValueAsString(subscription))
                .post("/subscription")
                .then()
                .statusCode(200);

        SubscriptionResource.EmailVerificationReply reply = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("email", email)
                .get("/subscription/verify")
                .then()
                .statusCode(200)
                .extract().body().as(SubscriptionResource.EmailVerificationReply.class);
        assertThat(reply).isNotNull();
        assertThat(reply.isEmailExists()).isTrue();
        assertThat(reply.getEmail()).isEqualTo(email);

        Subscription aSubscription = given()
                .accept(ContentType.JSON)
                .queryParam("email", email)
                .get("/subscription")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(Subscription.class);

        assertThat(aSubscription).isNotNull();
        assertThat(aSubscription.getId()).isEqualTo(id);
        assertThat(aSubscription.getEmail()).isEqualTo(email);
        assertThat(aSubscription.isVerified()).isFalse();

        aSubscription = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(objectMapper.writeValueAsString(subscription))
                .put("/subscription/confirm")
                .then()
                .statusCode(200)
                .extract().body().as(Subscription.class);

        assertThat(aSubscription).isNotNull();
        assertThat(aSubscription.getId()).isEqualTo(id);
        assertThat(aSubscription.getEmail()).isEqualTo(email);
        assertThat(aSubscription.isVerified()).isTrue();
    }

}
