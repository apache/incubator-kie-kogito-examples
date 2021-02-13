/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kogito.examples.sw.github.service;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class GitHubResourceTest {

    @Test
    void addLabels() {
        given().when()
                .body("[ \"bug\", \"documentation\" ]")
                .contentType(ContentType.JSON)
                .post("/repo/john/amazing-repo/pr/1/labels")
                .then()
                .statusCode(200);
    }

    @Test
    void addReviewers() {
        given().when()
                .body("[ \"john\", \"jane\" ]")
                .contentType(ContentType.JSON)
                .post("/repo/john/amazing-repo/pr/1/reviewers")
                .then()
                .statusCode(200);
    }

    @Test
    void fetchFiles() {
        given().when()
                .get("/repo/john/amazing-repo/pr/1/files")
                .then()
                .statusCode(200)
                .body(is("[\"myfile\"]"));
    }
}
