package org.kie.kogito.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

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
    
    @Inject
    @Named("demo.orderItems")
    Process<? extends Model> orderItemsProcess;

    @BeforeEach
    public void setup() {
        // abort up all intsances after each test
        // as other tests might have added instances
        // needed until Quarkust implements @DirtiesContext similar to springboot
        // see https://github.com/quarkusio/quarkus/pull/2866
        orderProcess.instances().values().forEach(pi -> pi.abort());
        orderItemsProcess.instances().values().forEach(pi -> pi.abort());
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
    
    @Test
    public void testOrdersWithErrorRest() {
        assertNotNull(orderProcess);

        // test adding new order
        String addOrderPayload = "{\"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addOrderPayload).when()
                .post("/orders").then().statusCode(500).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));
        
        // test retrieving error info using process management addon
        given().accept(ContentType.JSON).when().get("/management/process/demo.orders/instances/" + firstCreatedId + "/error").then()
        .statusCode(200).body("id", is(firstCreatedId));
        
        String fixedOrderPayload = "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/orders/" + firstCreatedId).then()
        .statusCode(200).body("id", is(firstCreatedId));
        
        given().accept(ContentType.JSON).when().post("/management/process/demo.orders/instances/" + firstCreatedId + "/retrigger").then()
        .statusCode(200);
        
        // delete second before finishing
        given().accept(ContentType.JSON).when().delete("/orders/" + firstCreatedId).then().statusCode(200);
        // get all orders make sure there is zero
        given().accept(ContentType.JSON).body(addOrderPayload).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(0));
    }
    
    @Test
    public void testOrdersWithOrderItemsRest() {
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

        // test getting order items subprocess
        String orderItemsId = given().accept(ContentType.JSON).body(addOrderPayload).when().get("/orderItems").then().statusCode(200)
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

        // test getting order items subprocess
        String orderItemsId = given().accept(ContentType.JSON).body(addOrderPayload).when().get("/orderItems").then().statusCode(200)
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