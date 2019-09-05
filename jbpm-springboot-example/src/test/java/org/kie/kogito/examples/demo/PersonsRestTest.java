package org.kie.kogito.examples.demo;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;

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

}