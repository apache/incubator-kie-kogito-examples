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
public class PersonsRestTest {
    @Inject
    @Named("persons")
    Process<? extends Model> personProcess;

    @BeforeEach
    public void setup() {
        // abort up all intsances after each test
        // as other tests might have added instances
        // needed until Quarkust implements @DirtiesContext similar to springboot
        // see https://github.com/quarkusio/quarkus/pull/2866
        personProcess.instances().values().forEach(pi -> pi.abort());
    }

    @Test
    public void testAdultPersonsRest() {
        assertNotNull(personProcess);

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
        assertNotNull(personProcess);

        // test new person
        String addPersonPayload = "{\"person\" : {\"name\" : \"Jane Doe\", \"age\" : 16}}";
        String firstCreatedId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addPersonPayload).when()
                .post("/persons").then().statusCode(200).body("id", notNullValue()).extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/persons").then().statusCode(200)
                .body("$.size()", is(1), "[0].id", is(firstCreatedId));
        
        // test getting task
        Map taskInfo = given().accept(ContentType.JSON).when().get("/persons/" + firstCreatedId + "/tasks").then()
        .statusCode(200).extract().as(Map.class);
        
        assertEquals(1, taskInfo.size());
        taskInfo.containsValue("ChildrenHandling");
        
        // test completing task
        String fixedOrderPayload = "{}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(fixedOrderPayload).when().post("/persons/" + firstCreatedId + "/ChildrenHandling/" + taskInfo.keySet().iterator().next()).then()
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

}