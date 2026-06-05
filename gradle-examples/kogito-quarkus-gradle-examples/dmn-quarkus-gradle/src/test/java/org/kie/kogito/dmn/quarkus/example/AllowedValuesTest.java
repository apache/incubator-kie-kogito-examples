/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.dmn.quarkus.example;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class AllowedValuesTest {

    @Test
    public void testAllowedValuesWithValidValue() {
        given()
                .body("{\n" +
                        "  \"p1\": {\n" +
                        "    \"Name\": \"Joe\",\n" +
                        "    \"Interests\": [\n" +
                        "      \"Golf\"\n" +
                        "    ]\n" +
                        "  }\n" +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/AllowedValuesChecksInsideCollection")
                .then()
                .statusCode(200)
                .body("'MyDecision'", is("The Person Joe likes 1 thing(s)."));
    }

    @Test
    public void testAllowedValuesWithInvalidValue() {
        given()
                .body("{\n" +
                        "  \"p1\": {\n" +
                        "    \"Name\": \"Joe\",\n" +
                        "    \"Interests\": [\n" +
                        "      \"Dancing\"\n" +
                        "    ]\n" +
                        "  }\n" +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/AllowedValuesChecksInsideCollection")
                .then()
                .statusCode(500)
                .body("messages[0].message", containsString(
                        "Error while evaluating node 'MyDecision' for dependency 'p1': the dependency value '{Interests=[Dancing], Name=Joe}' is not allowed by the declared type (DMNType{ http://www.trisotech.com/definitions/_238bd96d-47cd-4746-831b-504f3e77b442 : Person })"));
    }
}
