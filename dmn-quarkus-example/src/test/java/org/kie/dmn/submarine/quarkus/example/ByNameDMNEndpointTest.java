package org.kie.dmn.submarine.quarkus.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class ByNameDMNEndpointTest {

    @Test
    public void testGET() {
        given()
          .when()
               .get("/dmn/Traffic Violation")
          .then()
             .statusCode(200)
               .body("model-namespace", is("https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF"))
               .body("model-name", is("Traffic Violation"));
    }
    
    @Test
    public void testEvaluateTrafficViolation() {
        given()
               .body("{\n" +
                     "    \"Driver\": {\n" +
                     "        \"Points\": 2\n" +
                     "    },\n" +
                     "    \"Violation\": {\n" +
                     "        \"Type\": \"speed\",\n" +
                     "        \"Actual Speed\": 120,\n" +
                     "        \"Speed Limit\": 100\n" +
                     "    }\n" +
                     "}")
               .contentType(ContentType.JSON)
          .when()
               .post("/dmn/Traffic Violation")
          .then()
             .statusCode(200)
               .body("'dmn-context'.'Should the driver be suspended?'", is("No"))
               .body("decision-results", hasItem(allOf(hasEntry("decision-name", "Should the driver be suspended?"),
                                                       hasEntry("result", "No"))));
    }
}
