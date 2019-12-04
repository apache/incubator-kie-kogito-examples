package org.kie.kogito.examples.demo;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.kogito.examples.DemoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

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
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertNotNull(firstCreatedId);
    }

    @Test
    public void testGetOrders() {
        // create two orders
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertNotNull(firstCreatedId);

        String secondCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertNotNull(secondCreatedId);

        // get all orders
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()",
                      is(2));
    }

    @Test
    public void testGetOrderById() {
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertNotNull(firstCreatedId);

        // get order by its id and test
        given().accept(ContentType.JSON).body(orderPayload).when().get("/orders/" + firstCreatedId).then()
                .statusCode(200).body("id",
                                      is(firstCreatedId));
    }

    @Test
    public void testDeleteOrder() {
        // create two orders
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertNotNull(firstCreatedId);

        String secondCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertNotNull(secondCreatedId);

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
    
    @Test
    public void testOrdersWithErrorRest() {        

        // test adding new order
        String addOrderPayload = "{\"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addOrderPayload).when()
                .post("/orders").then().statusCode(500).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));
        
        // test retrieving error info using process management addon
        given().accept(ContentType.JSON).when().get("/management/processes/demo.orders/instances/" + firstCreatedId + "/error").then()
        .statusCode(200).body("id", is(firstCreatedId));
        
        String fixedOrderPayload = "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/orders/" + firstCreatedId).then()
        .statusCode(200).body("id", is(firstCreatedId));
        
        given().accept(ContentType.JSON).when().post("/management/processes/demo.orders/instances/" + firstCreatedId + "/retrigger").then()
        .statusCode(200);
        
        // delete second before finishing
        given().accept(ContentType.JSON).when().delete("/orders/" + firstCreatedId).then().statusCode(200);
        // get all orders make sure there is zero
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(0));
    }
    
    @Test
    public void testOrdersWithOrderItemsRest() {

        // test adding new order
        String addOrderPayload = "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addOrderPayload).when()
                .post("/orders").then().statusCode(200).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));

        // test getting order by id
        given().accept(ContentType.JSON).when().get("/orders/" + firstCreatedId).then()
                .statusCode(200).body("id", is(firstCreatedId));

        // test getting order items subprocess
        String orderItemsId = given().accept(ContentType.JSON).when().get("/orderItems").then().statusCode(200)
                .body("$.size()", is(1)).extract().path("[0].id");
        
        // test getting order items by id
        given().accept(ContentType.JSON).when().get("/orderItems/" + orderItemsId).then()
                .statusCode(200).body("id", is(orderItemsId));
        
        // test getting task
        Map taskInfo = given().accept(ContentType.JSON).when().get("/orderItems/" + orderItemsId + "/tasks").then()
        .statusCode(200).extract().as(Map.class);
        
        assertEquals(1, taskInfo.size());
        taskInfo.containsValue("Verify_order");
        
        // test completing task
        String payload = "{}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(payload).when().post("/orderItems/" + orderItemsId + "/Verify_order/" + taskInfo.keySet().iterator().next()).then()
        .statusCode(200).body("id", is(orderItemsId));
        
        // get all orders make sure there is zero
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(0));
        
        // get all order items make sure there is zero
        given().accept(ContentType.JSON).when().get("/orderItems").then().statusCode(200)
                .body("$.size()", is(0));
    }
    
    @Test
    public void testOrdersWithOrderItemsAbortedRest() {

        // test adding new order
        String addOrderPayload = "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addOrderPayload).when()
                .post("/orders").then().statusCode(200).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));

        // test getting order by id
        given().accept(ContentType.JSON).when().get("/orders/" + firstCreatedId).then()
                .statusCode(200).body("id", is(firstCreatedId));

        // test getting order items subprocess
        String orderItemsId = given().accept(ContentType.JSON).when().get("/orderItems").then().statusCode(200)
                .body("$.size()", is(1)).extract().path("[0].id");
        
        // test getting order items by id
        given().accept(ContentType.JSON).when().get("/orderItems/" + orderItemsId).then()
                .statusCode(200).body("id", is(orderItemsId));
        
        // test getting task
        Map taskInfo = given().accept(ContentType.JSON).when().get("/orderItems/" + orderItemsId + "/tasks").then()
        .statusCode(200).extract().as(Map.class);
        
        assertEquals(1, taskInfo.size());
        taskInfo.containsValue("Verify_order");
        
        // test deleting order items
        given().accept(ContentType.JSON).when().delete("/orderItems/" + orderItemsId).then().statusCode(200);
        
        // get all orders make sure there is zero
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(0));
        
        // get all order items make sure there is zero
        given().accept(ContentType.JSON).when().get("/orderItems").then().statusCode(200)
                .body("$.size()", is(0));
    }
}