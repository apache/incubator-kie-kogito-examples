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
package org.kie.kogito.examples;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusIntegrationTest
public class ErrorRestIT {

    @Test
    public void testErrorRest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"number\" : 12345}").when()
                .post("/error")
                .then()
                .statusCode(201)
                .body("workflowdata.numberType", is("odd"));
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"number\" : 4}").when()
                .post("/error")
                .then()
                .statusCode(201)
                .body("workflowdata.numberType", is("even"));
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"number\" : 2}").when()
                .post("/error")
                .then()
                .statusCode(201)
                .body("workflowdata.numberType", is("even"));
    }
    
    @Test
    public void testErrorWithMetadata() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"number\" : 12342}")
                .when()
                .post("/errorWithMetadata")
                .then()
                .statusCode(201);
        
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"number\" : 12341}")
                .when()
                .post("/errorWithMetadata")
                .then()
                .statusCode(400);
    }
}
