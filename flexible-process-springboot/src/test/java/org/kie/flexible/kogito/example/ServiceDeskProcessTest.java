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

package org.kie.flexible.kogito.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.flexible.kogito.example.model.Product;
import org.kie.flexible.kogito.example.model.State;
import org.kie.flexible.kogito.example.model.SupportCase;
import org.kie.flexible.kogito.example.service.TriageService;
import org.kie.kogito.tests.KogitoSpringbootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = KogitoSpringbootApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class ServiceDeskProcessTest {
    
    private static final String BASE_PATH = "/serviceDesk";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testSupportCaseExample() {
        String id = createSupportCase();
        addSupportComment(id);
        addCustomerComment(id);
        resolveCase(id);
        sendQuestionnaire(id);

        checkAllProcessesFinished();
    }

    private String createSupportCase() {
        SupportCase supportCase = new SupportCase()
                .setProduct(new Product().setFamily("Middleware").setName("Kogito"))
                .setCustomer("Paco")
                .setDescription("Something is not working");
        Map<String, Object> params = new HashMap<>();
        params.put("supportCase", supportCase);

        return given()
                .contentType(ContentType.JSON)
            .when()
                .body(params)
                .post(BASE_PATH)
            .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("supportCase.state", is(State.WAITING_FOR_OWNER.name()))
                .body("supportCase.engineer", anyOf(is(TriageService.KOGITO_ENGINEERS[0]), is(TriageService.KOGITO_ENGINEERS[1])))
                .body("supportGroup", is("Kogito")).extract().path("id");
    }

    private void addSupportComment(String id) {
        String link = given()
                .basePath(BASE_PATH + "/" + id)
                .contentType(ContentType.JSON)
            .when()
                .post("/ReceiveSupportComment")
            .then()
                .statusCode(200)
                .header("Link", notNullValue())
            .extract()
                .header("Link");

        String taskPath = link.substring(1, link.indexOf(">"));
        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Have you tried to turn it off and on again?");

        given()
            .basePath(BASE_PATH)
            .queryParam("user", "kelly")
            .queryParam("group", "support")
            .contentType(ContentType.JSON)
        .when()
            .body(params)
            .post(taskPath)
        .then()
            .statusCode(200)
            .body("supportCase.state", is(State.WAITING_FOR_CUSTOMER.name()))
            .body("supportCase.comments[0].text", is(params.get("comment")))
            .body("supportCase.comments[0].author", is("kelly"))
            .body("supportCase.comments[0].date", notNullValue());
    }

    private void addCustomerComment(String id) {
        String link = given().basePath(BASE_PATH + "/" + id).contentType(ContentType.JSON).when()
                .post("/ReceiveCustomerComment").then().statusCode(200).header("Link", notNullValue()).extract()
                .header("Link");

        String taskPath = link.substring(1, link.indexOf(">"));
        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Great idea!");

        given()
            .basePath(BASE_PATH)
            .queryParam("user", "Paco")
            .queryParam("group", "customer")
            .contentType(ContentType.JSON)
        .when()
            .body(params)
            .post(taskPath).then().statusCode(200)
            .body("supportCase.state", is(State.WAITING_FOR_OWNER.name()))
            .body("supportCase.comments[1].text", is(params.get("comment")))
            .body("supportCase.comments[1].author", is("Paco"))
            .body("supportCase.comments[1].date", notNullValue());
    }

    private void resolveCase(String id) {
        given().basePath(BASE_PATH + "/" + id).contentType(ContentType.JSON).when().post("/Resolve_Case").then()
                .statusCode(200).body("supportCase.state", is(State.RESOLVED.name()));
    }

    @SuppressWarnings("unchecked")
    private void sendQuestionnaire(String id) {
        Map<String, String> tasks = given()
                .basePath(BASE_PATH + "/" + id)
                .contentType(ContentType.JSON)
            .when()
                .get("/tasks")
                .as(Map.class);

        assertEquals(1, tasks.size());
        assertTrue(tasks.values().contains("Questionnaire"));
        Optional<String> taskId = tasks.entrySet().stream()
            .filter(e -> e.getValue().equals("Questionnaire"))
            .map(Map.Entry::getKey)
            .findFirst();
        assertTrue(taskId.isPresent());

        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Kogito is great!");
        params.put("evaluation", 10);

        given()
            .basePath(BASE_PATH + "/" + id)
            .queryParam("user", "Paco")
            .queryParam("group", "customer")
            .contentType(ContentType.JSON)
        .when()
            .body(params)
            .post("/Questionnaire/" + taskId.get())
        .then()
            .statusCode(200)
            .body("supportCase.state", is(State.CLOSED.name()))
            .body("supportCase.questionnaire.comment", is(params.get("comment")))
            .body("supportCase.questionnaire.evaluation", is(params.get("evaluation")))
            .body("supportCase.questionnaire.date", notNullValue());
    }

    private void checkAllProcessesFinished() {
        List<?> processes = given()
                .basePath(BASE_PATH)
                .contentType(ContentType.JSON)
            .when()
                .get("/")
            .as(List.class);

        assertTrue(processes.isEmpty());
    }
}