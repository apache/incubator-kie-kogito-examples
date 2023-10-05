/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.acme.examples;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.acme.examples.sw.custom.CalculatorServer;
import org.kie.kogito.serverless.workflow.SWFConstants;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusIntegrationTest
class CustomRestIT {

    private static CalculatorServer server; 
    
    @BeforeAll 
    static void init() throws IOException {
        server = new CalculatorServer(8082);
    }
    
    
    @AfterAll 
    static void cleanup () throws IOException {
        server.close();
    }
    
    
    @Test
    void testCustomType() {
        testIt("customType");
    }
    
    @Test
    void testCustomFunction() {
        testIt("customFunction");
    }
    

    private void testIt(String path) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(Map.of(SWFConstants.DEFAULT_WORKFLOW_VAR, Map.of("dividend", 4, "divisor", 3)))
                .post(path)
                .then()
                .statusCode(201)
                .body("workflowdata.response", is(1));
    }
}
