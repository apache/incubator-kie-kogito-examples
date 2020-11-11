package org.acme.sw.onboarding.queries;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;

@QuarkusTest
class AssignmentUnitTest {

    @Test
    public void verifyChildAssignment() {
        given()
                .body("{ \"patients\": [{ \"name\": \"Mick\", \"dateOfBirth\": \"2017-08-15\", \"gender\": \"MALE\"}] }")
                .contentType(ContentType.JSON)
                .when()
                .post("/find-assigned")
                .then()
                .statusCode(200)
                .body("assignedDoctorId", hasItem("505813da-2386-11eb-adc1-0242ac120002"));
    }
}