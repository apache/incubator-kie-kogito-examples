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

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.Model;
import org.kie.kogito.examples.DemoApplication;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.testcontainers.springboot.InfinispanSpringBootTestResource;
import org.kie.kogito.testcontainers.springboot.KafkaSpringBootTestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@ContextConfiguration(initializers = {InfinispanSpringBootTestResource.Conditional.class, KafkaSpringBootTestResource.Conditional.class})
public class OrdersRestIT {

    protected static String orderPayload = "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";

    // restassured needs to know the random port created for test
    @LocalServerPort
    int port;

    @Autowired
    @Qualifier("demo.orders")
    Process<? extends Model> orderProcess;

    @Autowired
    @Qualifier("demo.orderItems")
    Process<? extends Model> orderItemsProcess;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        // need it when running with persistence
        orderProcess.instances().values(ProcessInstanceReadMode.MUTABLE).forEach(pi -> pi.abort());
        orderItemsProcess.instances().values(ProcessInstanceReadMode.MUTABLE).forEach(pi -> pi.abort());
    }

    @Test
    public void testOrdersRest() {
        assertNotNull(orderProcess);

        // test adding new order
        String addOrderPayload = "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        String firstCreatedId = given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(addOrderPayload).when()
                    .post("/orders")
                .then()
                    .statusCode(201)
                    .header("Location", notNullValue())
                .extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));

        // test getting order by id
        given()
                .accept(ContentType.JSON)
            .when()
                .get("/orders/{id}", firstCreatedId)
            .then()
                .statusCode(200)
                .body("id", is(firstCreatedId));

        // test delete order
        // first add second order...
        String secondCreatedId =
                given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(addOrderPayload)
                .when()
                    .post("/orders")
                .then()
                    .statusCode(201)
                .extract()
                    .path("id");
        // now delete the first order created
        given()
                .accept(ContentType.JSON)
            .when()
                .delete("/orders/{id}", firstCreatedId)
            .then()
                .statusCode(200);
        // get all orders make sure there is only one
        given()
                .accept(ContentType.JSON)
            .when()
                .get("/orders")
            .then()
                .statusCode(200)
                .body("$.size()", is(1), "[0].id", is(secondCreatedId));

        // delete second before finishing
        given()
                .accept(ContentType.JSON)
            .when()
                .delete("/orders/{id}", secondCreatedId)
            .then()
                .statusCode(200);
        // get all orders make sure there is zero
        given()
                .accept(ContentType.JSON)
            .when()
                .get("/orders")
            .then()
                .statusCode(200)
                .body("$.size()", is(0));
    }

    @Test
    public void testOrdersWithErrorRest() {
        assertNotNull(orderProcess);

        // test adding new order
        String addOrderPayload = "{\"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        String firstCreatedId = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(addOrderPayload).when()
                .post("/orders")
            .then()
                .statusCode(500)
                .body("id", notNullValue())
            .extract()
                .path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));

        // test retrieving error info using process management addon
        given().accept(ContentType.JSON).when().get("/management/processes/demo.orders/instances/" + firstCreatedId + "/error").then()
        .statusCode(200).body("id", is(firstCreatedId));

        String fixedOrderPayload = "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().put("/orders/" + firstCreatedId).then()
        .statusCode(200)
                .body("id", is(firstCreatedId))
                .body("approver", equalTo("john"))
                .body("order.orderNumber", equalTo("12345"))
                .body("order.shipped", equalTo(false));

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
        assertNotNull(orderProcess);

        // test adding new order
        String addOrderPayload = "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addOrderPayload).when()
                .post("/orders").then().statusCode(201).body("id", notNullValue()).extract().path("id");

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
        String taskInfo = given()
            .accept(ContentType.JSON)
            .when()
            .get("/orderItems/" + orderItemsId + "/tasks?user=john")
            .then()
            .statusCode(200)
            .body("$.size", is(1))
            .body("[0].name", is("Verify order"))
            .extract()
            .path("[0].id");

        // test completing task
        String payload = "{}";
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(payload)
            .when()
            .post("/orderItems/" + orderItemsId + "/Verify_order/" + taskInfo + "?user=john")
            .then()
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
                .post("/orders").then().statusCode(201).body("id", notNullValue()).extract().path("id");

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
        given()
            .accept(ContentType.JSON)
            .when()
            .get("/orderItems/" + orderItemsId + "/tasks?user=john")
            .then()
            .statusCode(200)
            .body("$.size", is(1))
            .body("[0].name", is("Verify order"));

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
    public void testCreateAndUpdateOrders() {
        String orderPayload = "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        String id = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
                .post("/orders").then().statusCode(201).body("id",
                                                             notNullValue()).extract().path("id");

        assertNotNull(id);
        // get all orders make sure there is one
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(1));

        // get order by its custom ID and test
        given().accept(ContentType.JSON).body(orderPayload).when().get("/orders/{id}", id).then()
                .statusCode(200).body("id",
                                      is(id));
        // update the instance
        orderPayload = "{\"approver\" : \"joe\", \"order\" : {\"orderNumber\" : \"54321\", \"shipped\" : true}}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(orderPayload).when()
        .put("/orders/{id}", id).then()
                .statusCode(200)
                .body("approver", equalTo("joe"))
                .body("order.orderNumber", equalTo("54321"))
                .body("order.shipped", equalTo(true));

        // get all orders make sure there is one
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(1));

        // test deleting order items by custom ID
        given().accept(ContentType.JSON).when().delete("/orders/{id}", id).then().statusCode(200);

        // get all orders make sure there is zero
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("$.size()", is(0));
    }
}