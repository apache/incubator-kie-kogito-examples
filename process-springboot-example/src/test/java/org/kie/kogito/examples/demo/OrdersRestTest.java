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
package org.kie.kogito.examples.demo;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.Model;
import org.kie.kogito.examples.DemoApplication;
import org.kie.kogito.process.Process;
import org.kie.kogito.testcontainers.InfinispanContainer;
import org.kie.kogito.testcontainers.KogitoKafkaContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class OrdersRestTest {

    @Container
    private static final GenericContainer<?> INFINISPAN = new InfinispanContainer().enableConditional();

    @Container
    private static final KogitoKafkaContainer KAFKA = new KogitoKafkaContainer().enableConditional();

    // restassured needs to know the random port created for test
    @LocalServerPort
    int port;

    @Inject
    @Named("demo.orders")
    Process<? extends Model> orderProcess;

    @Inject
    @Named("demo.orderItems")
    Process<? extends Model> orderItemsProcess;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        // need it when running with persistence
        orderProcess.instances().values().forEach(pi -> pi.abort());
        orderItemsProcess.instances().values().forEach(pi -> pi.abort());
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
        Map taskInfo = given().accept(ContentType.JSON).when().get("/orderItems/" + orderItemsId + "/tasks?user=john").then()
        .statusCode(200).extract().as(Map.class);
        
        assertEquals(1, taskInfo.size());
        taskInfo.containsValue("Verify_order");
        
        // test completing task
        String payload = "{}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(payload).when().post("/orderItems/" + orderItemsId + "/Verify_order/" + taskInfo.keySet().iterator().next() + "?user=john").then()
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
        Map taskInfo = given().accept(ContentType.JSON).when().get("/orderItems/" + orderItemsId + "/tasks?user=john").then()
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
    
    @Test
    public void testCreateAndGetOrderByBusinessKey() {
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders?businessKey=ORD-0001").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertNotNull(firstCreatedId);

        // get order by its business key and test
        given().accept(ContentType.JSON).body(orderPayload).when().get("/orders/ORD-0001").then()
                .statusCode(200).body("id",
                                      is(firstCreatedId));
        
        // test deleting order items by business key
        given().accept(ContentType.JSON).when().delete("/orders/ORD-0001").then().statusCode(200);
        
        // get all orders make sure there is zero
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(0));
    }
    
    @Test
    public void testCreateDuplicateOrders() {
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders?businessKey=ORD-0001").then().statusCode(200).body("id",
                                                             notNullValue()).extract().path("id");

        assertNotNull(firstCreatedId);
        // get all orders make sure there is one
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(1));

        // get order by its business key and test
        given().accept(ContentType.JSON).body(orderPayload).when().get("/orders/ORD-0001").then()
                .statusCode(200).body("id",
                                      is(firstCreatedId));
        // create another instance with same business key which should fail with conflict response code
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
        .post("/orders?businessKey=ORD-0001").then().statusCode(409);
        
        // get all orders make sure there is one
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(1));
        
        // test deleting order items by business key
        given().accept(ContentType.JSON).when().delete("/orders/ORD-0001").then().statusCode(200);
        
        // get all orders make sure there is zero
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(0));
    }
}