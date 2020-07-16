package org.kie.kogito.examples;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
public class HelloTest {

    @Inject
    MyAgendaEventListener listener;

    @Test
    public void testHelloEndpoint() {
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body("{\"strings\":[\"hello\"]}")
          .when()
                .post("/hello")
          .then()
             .statusCode(200)
             .body(is("[\"hello\",\"world\"]"));

        assertThat(listener.counter.get(), is(1));
    }

}