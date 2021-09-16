/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.examples;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

@QuarkusTest
public class RestQueryTest {

    private static final String JSON_PAYLOAD_MAIN =
            "  \"loanApplications\":[\n" +
                    "    {\n" +
                    "      \"id\":\"ABC10001\",\n" +
                    "      \"amount\":2000,\n" +
                    "      \"deposit\":100,\n" +
                    "      \"applicant\":{\n" +
                    "        \"age\":45,\n" +
                    "        \"name\":\"John\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"id\":\"ABC10002\",\n" +
                    "      \"amount\":5000,\n" +
                    "      \"deposit\":100,\n" +
                    "      \"applicant\":{\n" +
                    "        \"age\":25,\n" +
                    "        \"name\":\"Paul\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"id\":\"ABC10015\",\n" +
                    "      \"amount\":1000,\n" +
                    "      \"deposit\":100,\n" +
                    "      \"applicant\":{\n" +
                    "        \"age\":12,\n" +
                    "        \"name\":\"George\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ]\n";

    private static final String JSON_PAYLOAD_A =
            "{\n" +
                    "  \"division\":\"DivisionA\",\n" +
                    JSON_PAYLOAD_MAIN +
                    "}";

    private static final String JSON_PAYLOAD_B =
            "{\n" +
                    "  \"division\":\"DivisionB\",\n" +
                    JSON_PAYLOAD_MAIN +
                    "}";

    @Test
    public void testApprovedDivisionA() {
        given()
                .body(JSON_PAYLOAD_A)
                .contentType(ContentType.JSON)
                .when()
                .post("/custom")
                .then()
                .statusCode(200)
                .body("id", hasItem("ABC10001"));
    }

    @Test
    public void testApprovedDivisionB() {
        given()
                .body(JSON_PAYLOAD_B)
                .contentType(ContentType.JSON)
                .when()
                .post("/custom")
                .then()
                .statusCode(200)
                .body("id", hasItems("ABC10001", "ABC10002"));
    }
}
