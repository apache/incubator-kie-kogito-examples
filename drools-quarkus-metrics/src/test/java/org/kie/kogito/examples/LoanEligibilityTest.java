package org.kie.kogito.examples;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class LoanEligibilityTest {

    @Test
    public void testEvaluateTrafficViolation() {
        given()
                .body("{" +
                          "\"Client\": " +
                            "{\"age\": 43,\"salary\": 1950,\"existing payments\": 100}," +
                            "\"Loan\": {\"duration\": 15,\"installment\": 180}, " +
                            "\"God\" : \"Yes\", " +
                            "\"Bribe\": 1000" +
                          "}"
                )
                .contentType(ContentType.JSON)
                .when()
                .post("/LoanEligibility")
                .then()
                .statusCode(200)
                .body("'Decide'", is(true));
    }

//    @Test
//    public void testMetricsTrafficViolation() {
//        given()
//                .when()
//                .get("/metrics")
//                .then()
//                .statusCode(200)
//                .body(containsString("string_dmn_result{decision=\"Judgement\",identifier=\"Yes\",} 1.0"))
//                .body(containsString("number_dmn_result{decision=\"Is Enought?\",quantile=\"0.1\",} 100.0"))
//                .body(containsString("api_http_response_code{handler=\"LoanEligibility\",identifier=\"200\",} 1.0"));
//    }
}
