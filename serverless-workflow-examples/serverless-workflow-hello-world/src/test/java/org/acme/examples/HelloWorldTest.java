package org.acme.examples;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class HelloWorldTest {

    @BeforeAll
    static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testHelloEndpoint() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when().post("/hello_world")
                .then()
                .statusCode(201)
                .body("workflowdata.greeting", is("Hello World"))
                .body("workflowdata.mantra", is("Serverless Workflow is awesome!"));
    }
}
