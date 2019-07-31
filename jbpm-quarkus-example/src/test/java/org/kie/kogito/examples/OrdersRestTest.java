package org.kie.kogito.examples;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class OrdersRestTest {
    @Inject
    @Named("demo.orders")
    Process<? extends Model> orderProcess;

    @BeforeEach
    public void setup() {
        // abort up all intsances after each test
        // as other tests might have added instances
        // needed until Quarkust implements @DirtiesContext similar to springboot
        // see https://github.com/quarkusio/quarkus/pull/2866
        orderProcess.instances().values().forEach(pi -> pi.abort());
    }

    @Test
    public void testOrdersRest() {
        assertNotNull(orderProcess);

        // test adding new order
        String addOrderPayload = "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addOrderPayload).when()
                .post("/orders").then().statusCode(200).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).body(addOrderPayload).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));

        // test getting order by id
        given().accept(ContentType.JSON).body(addOrderPayload).when().get("/orders/" + firstCreatedId).then()
                .statusCode(200).body("id", is(firstCreatedId));

        // test delete order
        // first add second order...
        String secondCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addOrderPayload)
                .when().post("/orders").then().statusCode(200).body("id", notNullValue()).extract().path("id");
        // now delete the first order created
        given().accept(ContentType.JSON).when().delete("/orders/" + firstCreatedId).then().statusCode(200);
        // get all orders make sure there is only one
        given().accept(ContentType.JSON).body(addOrderPayload).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(secondCreatedId));

        // delete second before finishing
        given().accept(ContentType.JSON).when().delete("/orders/" + secondCreatedId).then().statusCode(200);
        // get all orders make sure there is zero
        given().accept(ContentType.JSON).body(addOrderPayload).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(0));
    }

}