/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

@SuppressWarnings("rawtypes")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class PersonsRestTest {

 // restassured needs to know the random port created for test
    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testAdultPersonsRest() {
        

        // test new person
        String addPersonPayload = "{\"person\" : {\"name\" : \"John Doe\", \"age\" : 20}}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addPersonPayload).when()
                .post("/persons").then().statusCode(200).body("id", notNullValue()).extract().path("id");

        // get all persons make sure there is zero
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(0));
    }
    
    @Test
    public void testChildPersonsRest() {
        

        // test new person
        String addPersonPayload = "{\"person\" : {\"name\" : \"Jane Doe\", \"age\" : 16}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addPersonPayload).when()
                .post("/persons").then().statusCode(200).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));
        
        // test getting task
        Map taskInfo = given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId + "/tasks?user=admin").then()
        .statusCode(200).extract().as(Map.class);
        
        assertEquals(1, taskInfo.size());
        taskInfo.containsValue("ChildrenHandling");
        
        // test completing task
        String fixedOrderPayload = "{}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo.keySet().iterator().next() + "?user=admin").then()
        .statusCode(200).body("id", is(firstCreatedId));
     
        // get all persons make sure there is zero
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(0));
    }
    
    @Test
    public void testChildPersonsRestWithSecurityPolicy() {
        

        // test new person
        String addPersonPayload = "{\"person\" : {\"name\" : \"Jane Doe\", \"age\" : 16}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addPersonPayload).when()
                .post("/persons").then().statusCode(200).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));
        
        // test getting task
        Map taskInfo = given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId + "/tasks?user=admin").then()
        .statusCode(200).extract().as(Map.class);
        
        assertEquals(1, taskInfo.size());
        taskInfo.containsValue("ChildrenHandling");
        
        // test completing task
        String fixedOrderPayload = "{}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo.keySet().iterator().next() + "?user=admin").then()
        .statusCode(200).body("id", is(firstCreatedId));
     
        // get all persons make sure there is zero
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(0));
    }
    
    @Test
    public void testChildPersonsRestWithSecurityPolicyNotAuthorized() {
        

        // test new person
        String addPersonPayload = "{\"person\" : {\"name\" : \"Jane Doe\", \"age\" : 16}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addPersonPayload).when()
                .post("/persons").then().statusCode(200).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));
        
        // test getting task with wrong user
        Map taskInfo = given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId + "/tasks?user=john").then()
                .statusCode(200).extract().as(Map.class);
        assertEquals(0, taskInfo.size());
        
        taskInfo = given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId + "/tasks?user=admin").then()
                .statusCode(200).extract().as(Map.class);
        
        assertEquals(1, taskInfo.size());
        taskInfo.containsValue("ChildrenHandling");
        
        // test completing task with wrong user
        String fixedOrderPayload = "{}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo.keySet().iterator().next() + "?user=john").then()
        .statusCode(403);
        
        // test completing task with correct user
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo.keySet().iterator().next() + "?user=admin").then()
        .statusCode(200).body("id", is(firstCreatedId));
     
        // get all persons make sure there is zero
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(0));
    }
    
    @Test
    public void testChildPersonsRestWithSecurityPolicyAndLifeCycles() {
        

        // test new person
        String addPersonPayload = "{\"person\" : {\"name\" : \"Jane Doe\", \"age\" : 16}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addPersonPayload).when()
                .post("/persons").then().statusCode(200).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));
        
        // test getting task
        Map taskInfo = given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId + "/tasks?user=admin").then()
        .statusCode(200).extract().as(Map.class);
        
        assertEquals(1, taskInfo.size());
        taskInfo.containsValue("ChildrenHandling");
        
        String taskId = (String) taskInfo.keySet().iterator().next();
        
        // test claim task
        String fixedOrderPayload = "{}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskId + "?phase=claim&user=admin").then()
        .statusCode(200).body("id", is(firstCreatedId));
        // test release task
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskId + "?phase=release&user=admin").then()
        .statusCode(200).body("id", is(firstCreatedId));
        // test skip
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskId + "?phase=skip&user=admin").then()
        .statusCode(200).body("id", is(firstCreatedId));
     
        // get all persons make sure there is zero
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(0));
    }
    
    @Test
    public void testPersonsRestStartFromUserTask() {
        
        // test new person
        String addPersonPayload = "{\"person\" : {\"name\" : \"Jane Doe\", \"age\" : 30}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .header("X-KOGITO-StartFromNode", "UserTask_1")// this instructs to start from user task and skip any node before it
                .body(addPersonPayload).when()
                .post("/persons").then().statusCode(200)
                .body("id", notNullValue(), "person.adult", is(false))// since rule evaluation was skipped adult is still false even though age is about the 18 limit
                .extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId), "[0].person.adult", is(false));
        
        // test getting task
        Map taskInfo = given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId + "/tasks?user=admin").then()
        .statusCode(200).extract().as(Map.class);
        
        assertEquals(1, taskInfo.size());
        taskInfo.containsValue("ChildrenHandling");
        
        // test completing task
        String fixedOrderPayload = "{}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo.keySet().iterator().next() + "?user=admin").then()
        .statusCode(200).body("id", is(firstCreatedId));
     
        // get all persons make sure there is zero
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(0));
    }
    

    @Test
    public void testChildPersonsRestAbortViaMgmtInterface() {

        // test new person
        String addPersonPayload = "{\"person\" : {\"name\" : \"Jane Doe\", \"age\" : 16}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(addPersonPayload).when()
                .post("/persons").then().statusCode(200)
                .body("id", notNullValue(), "person.adult", is(false))
                .extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId), "[0].person.adult", is(false));
        
        // test getting task        
        Map taskInfo = given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId + "/tasks?user=admin").then()
        .statusCode(200).extract().as(Map.class);
        
        assertEquals(1, taskInfo.size());
        taskInfo.containsValue("ChildrenHandling");
        
        // abort process instance via management interface        
        given().contentType(ContentType.JSON).accept(ContentType.JSON).when().delete("/management/processes/persons/instances/" + firstCreatedId).then()
        .statusCode(200).body("id", is(firstCreatedId));
     
        // get all persons make sure there is zero
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(0));
    }
    
    @Test
    public void testChildPersonsRestRetriggerNodeViaMgmtInterface() {

        // test new person
        String addPersonPayload = "{\"person\" : {\"name\" : \"Jane Doe\", \"age\" : 16}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(addPersonPayload).when()
                .post("/persons").then().statusCode(200)
                .body("id", notNullValue(), "person.adult", is(false))
                .extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId), "[0].person.adult", is(false));
        
        // test getting task
        Map taskInfo = given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId + "/tasks?user=admin").then()
        .statusCode(200).extract().as(Map.class);
        
        assertEquals(1, taskInfo.size());
        taskInfo.containsValue("ChildrenHandling");
        
        String nodeInstanceId = given().contentType(ContentType.JSON).accept(ContentType.JSON).when().get("/management/processes/persons/instances/" + firstCreatedId + "/nodeInstances").then()
        .statusCode(200).body("$.size()", is(1)).extract().path("[0].nodeInstanceId");
        
        // retrigger node instance via management interface        
        given().contentType(ContentType.JSON).accept(ContentType.JSON).when().post("/management/processes/persons/instances/" + firstCreatedId + "/nodeInstances/" + nodeInstanceId).then()
        .statusCode(200);
        
        taskInfo = given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId + "/tasks?user=admin").then()
                .statusCode(200).extract().as(Map.class);
        
        String retriggeredNodeInstanceId = given().contentType(ContentType.JSON).accept(ContentType.JSON).when().get("/management/processes/persons/instances/" + firstCreatedId + "/nodeInstances").then()
                .statusCode(200).body("$.size()", is(1)).extract().path("[0].nodeInstanceId");
        // since node instance was retriggered it must have different ids
        assertNotEquals(nodeInstanceId, retriggeredNodeInstanceId);
        
        // test completing task
        String fixedOrderPayload = "{}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo.keySet().iterator().next()+ "?user=admin").then()
        .statusCode(200).body("id", is(firstCreatedId));
     
        // get all persons make sure there is zero
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(0));
    }
    
    @Test
    public void testChildPersonsRestCancelAndTriggerNodeViaMgmtInterface() {

        // test new person
        String addPersonPayload = "{\"person\" : {\"name\" : \"Jane Doe\", \"age\" : 16}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(addPersonPayload).when()
                .post("/persons").then().statusCode(200)
                .body("id", notNullValue(), "person.adult", is(false))
                .extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId), "[0].person.adult", is(false));
        
        // test getting task
        Map taskInfo = given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId + "/tasks?user=admin").then()
        .statusCode(200).extract().as(Map.class);
        
        assertEquals(1, taskInfo.size());
        taskInfo.containsValue("ChildrenHandling");
        
        String nodeInstanceId = given().contentType(ContentType.JSON).accept(ContentType.JSON).when().get("/management/processes/persons/instances/" + firstCreatedId + "/nodeInstances").then()
        .statusCode(200).body("$.size()", is(1)).extract().path("[0].nodeInstanceId");
        
        // cancel node instance
        given().contentType(ContentType.JSON).accept(ContentType.JSON).when().delete("/management/processes/persons/instances/" + firstCreatedId + "/nodeInstances/" + nodeInstanceId).then()
        .statusCode(200);
        
        // then trigger new node instance via management interface        
        given().contentType(ContentType.JSON).accept(ContentType.JSON).when().post("/management/processes/persons/instances/" + firstCreatedId + "/nodes/UserTask_1").then()
        .statusCode(200);
        
        taskInfo = given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId + "/tasks?user=admin").then()
                .statusCode(200).extract().as(Map.class);
        
        String retriggeredNodeInstanceId = given().contentType(ContentType.JSON).accept(ContentType.JSON).when().get("/management/processes/persons/instances/" + firstCreatedId + "/nodeInstances").then()
                .statusCode(200).body("$.size()", is(1)).extract().path("[0].nodeInstanceId");
        // since node instance was retriggered it must have different ids
        assertNotEquals(nodeInstanceId, retriggeredNodeInstanceId);
        
        // test completing task
        String fixedOrderPayload = "{}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo.keySet().iterator().next() + "?user=admin").then()
        .statusCode(200).body("id", is(firstCreatedId));
     
        // get all persons make sure there is zero
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(0));
    }

}