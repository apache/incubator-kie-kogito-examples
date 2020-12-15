package org.kie.kogito.serverless;

import java.util.Collections;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(OperationsMockService.class)
class ConversationFlowTest {

    @BeforeAll
    static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void sanityVerification() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(
                        Collections
                                .singletonMap(
                                        "workflowdata",
                                        Collections.singletonMap("fahrenheit", "100")))
                .post("/fahrenheit_to_celsius?forceSync=true")
                .then()
                .statusCode(201);
    }
}
