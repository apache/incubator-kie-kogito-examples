package org.acme.travels;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.kogito.tests.KogitoInfinispanSpringbootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoInfinispanSpringbootApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) 
public class ApprovalsRestTest {
    
    @LocalServerPort
    int randomServerPort;
    
    @Before
    public void before() {
        RestAssured.port = randomServerPort;
    }
    
    @Test
    public void testStartApprovalUnauthorized() {

        
        given()
               .body("{\"traveller\" : {\"firstName\" : \"John\",\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\",\"nationality\" : \"American\",\"address\" : {\"street\" : \"main street\",\"city\" : \"Boston\",\"zipCode\" : \"10005\",\"country\" : \"US\"}}")
               .contentType(ContentType.JSON)
          .when()
               .post("/approvals")
          .then()
             .statusCode(401);
             
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void testStartApprovalAuthorized() {
       // start new approval
       String id = given()
               .header("Authorization", "Basic am9objpqb2hu")                   
               .body("{\"traveller\" : {\"firstName\" : \"John\",\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\",\"nationality\" : \"American\",\"address\" : {\"street\" : \"main street\",\"city\" : \"Boston\",\"zipCode\" : \"10005\",\"country\" : \"US\"}}}")                   
               .contentType(ContentType.JSON)               
          .when()
               .post("/approvals")
          .then()
             .statusCode(200)
             .body("id", notNullValue()).extract().path("id");
       // get all active approvals
       given()
           .header("Authorization", "Basic am9objpqb2hu")
           .accept(ContentType.JSON)           
       .when()
           .get("/approvals")
       .then()
           .statusCode(200)
           .body("$.size()", is(1), "[0].id", is(id));
       
       // get just started approval
       given()
           .header("Authorization", "Basic am9objpqb2hu")
           .accept(ContentType.JSON)
       .when()
           .get("/approvals/" + id)
       .then()
           .statusCode(200)
           .body("id", is(id));
       
       // tasks assigned in just started approval
       
       Map taskInfo = given()
               .header("Authorization", "Basic am9objpqb2hu")
               .accept(ContentType.JSON)               
           .when()
               .get("/approvals/" + id + "/tasks?user=admin&group=managers")
           .then()
               .statusCode(200).extract().as(Map.class);
               
       assertEquals(1, taskInfo.size());
       taskInfo.containsValue("firstLineApproval");
       
       // complete first task without authorization header as it authorization is managed on task level
       // thus user and group(s) must be provided
       String payload = "{}";
       given()
           .contentType(ContentType.JSON)
           .accept(ContentType.JSON)
           .body(payload)
       .when()
           .post("/approvals/" + id + "/firstLineApproval/" + taskInfo.keySet().iterator().next() + "?user=mary&group=managers")
       .then()
           .statusCode(200)
           .body("id", is(id));
       
       // lastly abort the approval
       given()
           .header("Authorization", "Basic am9objpqb2hu")
           .accept(ContentType.JSON)
       .when()
           .delete("/approvals/" + id)
       .then()
           .statusCode(200)
           .body("id", is(id));
    }
}
