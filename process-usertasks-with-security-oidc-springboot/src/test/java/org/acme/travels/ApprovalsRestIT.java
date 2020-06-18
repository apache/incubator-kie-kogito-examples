/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.acme.travels;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.AccessTokenResponse;
import org.kie.kogito.springboot.KogitoSpringbootApplication;
import org.kie.kogito.testcontainers.KeycloakContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ApprovalsRestIT {

    @Container
    public static final KeycloakContainer KEYCLOAK = new KeycloakContainer();

    @LocalServerPort
    int randomServerPort;
    
    @BeforeEach
    public void before() {
        RestAssured.port = randomServerPort;
    }
    
    @Test
    public void testStartApprovalUnauthorized() {
        given()
               .body("{\"traveller\" : {\"firstName\" : \"John\",\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\",\"nationality\" : \"American\",\"address\" : {\"street\" : \"main street\",\"city\" : \"Boston\",\"zipCode\" : \"10005\",\"country\" : \"US\"}}")
               .contentType(ContentType.JSON)
          .when()
               .post("/approvals")
          .then()
             .statusCode(401);
             
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void testStartApprovalAuthorized() {
       // start new approval
       String id = given()
               .auth().oauth2(getAccessToken("mary"))
               .body("{\"traveller\" : {\"firstName\" : \"John\",\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\",\"nationality\" : \"American\",\"address\" : {\"street\" : \"main street\",\"city\" : \"Boston\",\"zipCode\" : \"10005\",\"country\" : \"US\"}}}")                   
               .contentType(ContentType.JSON)               
          .when()
               .post("/approvals")
          .then()
             .statusCode(200)
             .body("id", notNullValue()).extract().path("id");

       // get all active approvals
       given()
               .auth().oauth2(getAccessToken("mary"))
               .accept(ContentType.JSON)
       .when()
           .get("/approvals")
       .then()
           .statusCode(200)
           .body("$.size()", is(1), "[0].id", is(id));
       
       // get just started approval
       given()
               .auth().oauth2(getAccessToken("mary"))
           .accept(ContentType.JSON)
       .when()
           .get("/approvals/" + id)
       .then()
           .statusCode(200)
           .body("id", is(id));
       
       // tasks assigned in just started approval
       
       Map taskInfo = given()
               .auth().oauth2(getAccessToken("mary"))
               .accept(ContentType.JSON)               
           .when()
               .get("/approvals/" + id + "/tasks?user=admin&group=managers")
           .then()
               .statusCode(200).extract().as(Map.class);
               
       assertEquals(1, taskInfo.size());
       taskInfo.containsValue("firstLineApproval");
       
       // complete first task without authorization header as it authorization is managed on task level
       // thus user and group(s) must be provided
       String payload = "{}";
       given()
           .auth().oauth2(getAccessToken("mary"))
           .contentType(ContentType.JSON)
           .accept(ContentType.JSON)
           .body(payload)
       .when()
           .post("/approvals/" + id + "/firstLineApproval/" + taskInfo.keySet().iterator().next() + "?user=mary&group=managers")
       .then()
           .statusCode(200)
           .body("id", is(id));
       
       // lastly abort the approval
       given()
           .auth().oauth2(getAccessToken("mary"))
           .accept(ContentType.JSON)
       .when()
           .delete("/approvals/" + id)
       .then()
           .statusCode(200)
           .body("id", is(id));
    }

    private String getAccessToken(String userName) {
        return given()
                .param("grant_type", "password")
                .param("username", userName)
                .param("password", userName)
                .param("client_id", "kogito-app")
                .param("client_secret", "secret")
                .when()
                .post("http://localhost:8281/auth/realms/kogito/protocol/openid-connect/token")
                .as(AccessTokenResponse.class).getToken();

    }


}
