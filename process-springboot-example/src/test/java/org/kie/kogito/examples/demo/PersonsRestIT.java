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
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("rawtypes")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@ContextConfiguration(initializers = {InfinispanSpringBootTestResource.Conditional.class, KafkaSpringBootTestResource.Conditional.class})
public class PersonsRestIT {

    @Autowired
    @Qualifier("persons")
    Process<? extends Model> personProcess;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        // need it when running with persistence
        personProcess.instances().values(ProcessInstanceReadMode.MUTABLE).forEach(pi -> pi.abort());
    }

    @Test
    public void testAdultPersonsRest() {
        assertNotNull(personProcess);

        // test new person
        String addPersonPayload = "{\"person\" : {\"name\" : \"John Doe\", \"age\" : 20}}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addPersonPayload).when()
                .post("/persons").then().statusCode(201).body("id", notNullValue(), "person.adult", is(true)).extract().path("id");

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
                .post("/persons").then().statusCode(201).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));

        // test getting task
        String taskInfo = given()
            .accept(ContentType.JSON)
            .when()
            .get("/persons/" + firstCreatedId + "/tasks?user=admin")
            .then()
            .statusCode(200)
            .body("$.size", is(1))
            .body("[0].name", is("ChildrenHandling"))
            .extract()
            .path("[0].id");


        // test completing task
        String fixedOrderPayload = "{}";
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(fixedOrderPayload)
            .when()
            .post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo + "?user=admin")
            .then()
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
                .post("/persons").then().statusCode(201).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));

        // test getting task
        String taskInfo = given()
            .accept(ContentType.JSON)
            .when()
            .get("/persons/" + firstCreatedId + "/tasks?user=admin")
            .then()
            .statusCode(200)
            .body("$.size", is(1))
            .body("[0].name", is("ChildrenHandling"))
            .extract()
            .path("[0].id");
        // test completing task
        String fixedOrderPayload = "{}";
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(fixedOrderPayload)
            .when()
            .post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo + "?user=admin")
            .then()
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
                .post("/persons").then().statusCode(201).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));

        // test getting task with wrong user
        given()
            .accept(ContentType.JSON)
            .when()
            .get("/persons/" + firstCreatedId + "/tasks?user=john")
            .then()
            .statusCode(200)
            .body("$.size", is(0));

        String taskInfo = given()
            .accept(ContentType.JSON)
            .when()
            .get("/persons/" + firstCreatedId + "/tasks?user=admin")
            .then()
            .statusCode(200)
            .body("$.size", is(1))
            .body("[0].name", is("ChildrenHandling"))
            .extract()
            .path("[0].id");



        // test completing task with wrong user
        String fixedOrderPayload = "{}";
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(fixedOrderPayload)
            .when()
            .post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo + "?user=john")
            .then()
        .statusCode(403);

        // test completing task with correct user
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(fixedOrderPayload)
            .when()
            .post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo + "?user=admin")
            .then()
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
                .post("/persons").then().statusCode(201).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));

        // test getting task
        String taskId = given()
            .accept(ContentType.JSON)
            .when()
            .get("/persons/" + firstCreatedId + "/tasks?user=admin")
            .then()
            .statusCode(200)
            .body("$.size", is(1))
            .body("[0].name", is("ChildrenHandling"))
            .extract()
            .path("[0].id");


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
                .post("/persons").then().statusCode(201)
                .body("id", notNullValue(), "person.adult", is(false))// since rule evaluation was skipped adult is still false even though age is about the 18 limit
                .extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId), "[0].person.adult", is(false));

        // test getting task
        String taskInfo = given()
            .accept(ContentType.JSON)
            .when()
            .get("/persons/" + firstCreatedId + "/tasks?user=admin")
            .then()
            .statusCode(200)
            .body("$.size", is(1))
            .body("[0].name", is("ChildrenHandling"))
            .extract()
            .path("[0].id");

        // test completing task
        String fixedOrderPayload = "{}";
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(fixedOrderPayload)
            .when()
            .post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo + "?user=admin")
            .then()
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
                .post("/persons").then().statusCode(201)
                .body("id", notNullValue(), "person.adult", is(false))
                .extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId), "[0].person.adult", is(false));

        // test getting task
        given()
            .accept(ContentType.JSON)
            .when()
            .get("/persons/" + firstCreatedId + "/tasks?user=admin")
            .then()
            .statusCode(200)
            .body("$.size", is(1))
            .body("[0].name", is("ChildrenHandling"));



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
                .post("/persons").then().statusCode(201)
                .body("id", notNullValue(), "person.adult", is(false))
                .extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId), "[0].person.adult", is(false));

        // test getting task
        String taskInfo = given()
            .accept(ContentType.JSON)
            .when()
            .get("/persons/" + firstCreatedId + "/tasks?user=admin")
            .then()
            .statusCode(200)
            .body("$.size", is(1))
            .body("[0].name", is("ChildrenHandling"))
            .extract()
            .path("[0].id");

        String nodeInstanceId = given().contentType(ContentType.JSON).accept(ContentType.JSON).when().get("/management/processes/persons/instances/" + firstCreatedId + "/nodeInstances").then()
        .statusCode(200).body("$.size()", is(1)).extract().path("[0].nodeInstanceId");

        // retrigger node instance via management interface
        given().contentType(ContentType.JSON).accept(ContentType.JSON).when().post("/management/processes/persons/instances/" + firstCreatedId + "/nodeInstances/" + nodeInstanceId).then()
        .statusCode(200);

        taskInfo = given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId + "/tasks?user=admin").then()
            .statusCode(200)
            .body("$.size", is(1))
            .body("[0].name", is("ChildrenHandling"))
            .extract()
            .path("[0].id");

        String retriggeredNodeInstanceId = given().contentType(ContentType.JSON).accept(ContentType.JSON).when().get("/management/processes/persons/instances/" + firstCreatedId + "/nodeInstances").then()
                .statusCode(200).body("$.size()", is(1)).extract().path("[0].nodeInstanceId");
        // since node instance was retriggered it must have different ids
        assertNotEquals(nodeInstanceId, retriggeredNodeInstanceId);

        // test completing task
        String fixedOrderPayload = "{}";
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(fixedOrderPayload)
            .when()
            .post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo + "?user=admin")
            .then()
        .statusCode(200).body("id", is(firstCreatedId));

        // get all persons make sure there is zero
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(0));
    }

    @Test
    public void testChildPersonsRestCancelAndTriggerNodeViaMgmtInterface() {
        assertNotNull(personProcess);

        // test new person
        String addPersonPayload = "{\"person\" : {\"name\" : \"Jane Doe\", \"age\" : 16}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(addPersonPayload).when()
                .post("/persons").then().statusCode(201)
                .body("id", notNullValue(), "person.adult", is(false))
                .extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId), "[0].person.adult", is(false));

        // test getting task
        String taskId = given()
            .accept(ContentType.JSON)
            .when()
            .get("/persons/" + firstCreatedId + "/tasks?user=admin")
            .then()
            .statusCode(200)
            .body("$.size", is(1))
            .body("[0].name", is("ChildrenHandling"))
            .extract()
            .path("[0].id");


        String nodeInstanceId = given().contentType(ContentType.JSON).accept(ContentType.JSON).when().get("/management/processes/persons/instances/" + firstCreatedId + "/nodeInstances").then()
        .statusCode(200).body("$.size()", is(1)).extract().path("[0].nodeInstanceId");

        // cancel node instance
        given().contentType(ContentType.JSON).accept(ContentType.JSON).when().delete("/management/processes/persons/instances/" + firstCreatedId + "/nodeInstances/" + nodeInstanceId).then()
        .statusCode(200);

        // then trigger new node instance via management interface
        given().contentType(ContentType.JSON).accept(ContentType.JSON).when().post("/management/processes/persons/instances/" + firstCreatedId + "/nodes/UserTask_1").then()
        .statusCode(200);

        taskId = given()
            .accept(ContentType.JSON)
            .when()
            .get("/persons/" + firstCreatedId + "/tasks?user=admin")
            .then()
            .statusCode(200)
            .body("$.size", is(1))
            .body("[0].name", is("ChildrenHandling"))
            .extract()
            .path("[0].id");

        String retriggeredNodeInstanceId = given().contentType(ContentType.JSON).accept(ContentType.JSON).when().get("/management/processes/persons/instances/" + firstCreatedId + "/nodeInstances").then()
                .statusCode(200).body("$.size()", is(1)).extract().path("[0].nodeInstanceId");
        // since node instance was retriggered it must have different ids
        assertNotEquals(nodeInstanceId, retriggeredNodeInstanceId);

        // test completing task
        String fixedOrderPayload = "{}";
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(fixedOrderPayload)
            .when()
            .post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskId + "?user=admin")
            .then()
        .statusCode(200).body("id", is(firstCreatedId));

        // get all persons make sure there is zero
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(0));
    }
}