package org.kie.kogito.examples.demo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.kogito.examples.DemoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class OrderServiceRestTest {

    // restassured needs to know the random port created for test
    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    protected static String orderPayload = "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";

    @Test
    public void testCreateNewOrder() {
        // create order
        int firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertEquals(1,
                     firstCreatedId);
    }

    @Test
    public void testGetOrders() {
        // create two orders
        int firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertEquals(1,
                     firstCreatedId);

        int secondCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertEquals(2,
                     secondCreatedId);

        // get all orders
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()",
                      is(2),
                      "[0].id",
                      is(firstCreatedId),
                      "[1].id",
                      is(secondCreatedId));
    }

    @Test
    public void testGetOrderById() {
        int firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertEquals(1,
                     firstCreatedId);

        // get order by its id and test
        given().accept(ContentType.JSON).body(orderPayload).when().get("/orders/" + firstCreatedId).then()
                .statusCode(200).body("id",
                                      is(firstCreatedId));
    }

    @Test
    public void testDeleteOrder() {
        // create two orders
        int firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertEquals(1,
                     firstCreatedId);

        int secondCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertEquals(2,
                     secondCreatedId);

        // delete first order
        given().accept(ContentType.JSON).when().delete("/orders/" + firstCreatedId).then().statusCode(200);

        // get all orders make sure there is only one
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()",
                      is(1),
                      "[0].id",
                      is(secondCreatedId));

        // delete second before finishing
        given().accept(ContentType.JSON).when().delete("/orders/" + secondCreatedId).then().statusCode(200);
        // get all orders make sure there is zero
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()",
                      is(0));
    }
}