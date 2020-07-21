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
package org.kie.kogito.examples;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.testcontainers.quarkus.InfinispanQuarkusTestResource;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@SuppressWarnings("rawtypes")
@QuarkusTest
@QuarkusTestResource(value = InfinispanQuarkusTestResource.Conditional.class)
@QuarkusTestResource(value = KafkaQuarkusTestResource.Conditional.class)
public class PersonsRestIT {

    @Inject
    @Named("persons")
    Process<? extends Model> personProcess;

    @BeforeEach
    public void cleanUp() {
        // need it when running with persistence
        personProcess.instances().values().forEach(pi -> pi.abort());
    }

    @Test
    public void testAdultPersonsRest() {
        assertNotNull(personProcess);

        // test new person
        String addPersonPayload = "{\"person\" : {\"name\" : \"John Doe\", \"age\" : 20}}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addPersonPayload).when()
                .post("/persons").then().statusCode(200).body("id", notNullValue(), "person.adult", is(true)).extract().path("id");

        // get all persons make sure there is zero
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(0));
    }
    
    @Test
    public void testChildPersonsRest() {
        assertNotNull(personProcess);

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
        assertNotNull(personProcess);

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
        assertNotNull(personProcess);

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
        assertNotNull(personProcess);

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
    
    // Disabled until KOGITO-1787 is fixed
    @DisabledIfSystemProperty(named = "tests.category", matches = "persistence")
    @Test
    public void testPersonsRestStartFromUserTask() {
        assertNotNull(personProcess);

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
        assertNotNull(personProcess);

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
        assertNotNull(personProcess);

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
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo.keySet().iterator().next() + "?user=admin").then()
        .statusCode(200).body("id", is(firstCreatedId));
     
        // get all persons make sure there is zero
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(0));
    }
    
    // Disabled until KOGITO-1796 is fixed
    @DisabledIfSystemProperty(named = "tests.category", matches = "persistence")
    @Test
    public void testChildPersonsRestCancelAndTriggerNodeViaMgmtInterface() {
        assertNotNull(personProcess);

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