package org.kie.dmn.submarine.quarkus.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class GenericDMNEndpointTest {

    @Test
    public void testGET() {
        given()
          .when()
              .get("/dmn")
          .then()
             .statusCode(200)
               .body("models", hasSize(greaterThanOrEqualTo(1)));
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
               .header("X-DMN-model-namespace", "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF")
               .header("X-DMN-model-name", "Traffic Violation")
               .post("/dmn")
          .then()
             .statusCode(200)
               .body("'dmn-context'.'Should the driver be suspended?'", is("No"))
               .body("decision-results", hasItem(allOf(hasEntry("decision-name", "Should the driver be suspended?"),
                                                       hasEntry("result", "No"))));
    }
}
