package org.acme.travels;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class ApprovalsRestTest {
    
    
    @Test
    public void testStartApprovalUnauthorized() {

        
        given()
               .body("{\"traveller\" : {\"firstName\" : \"John\",\"lastName\" : \"Doe\",\"email\" : \"john.doe@example.com\",\"nationality\" : \"American\",\"address\" : {\"street\" : \"main street\",\"city\" : \"Boston\",\"zipCode\" : \"10005\",\"country\" : \"US\"}}}")
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
