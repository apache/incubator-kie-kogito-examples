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

package org.kogito.examples.sw.github.workflow;

import java.io.IOException;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(GitHubServiceMockServer.class)
class GitHubServiceTest {

    final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    GitHubService gitHubServiceBackend;

    @Test
    void addLabels() throws IOException {
        final JsonNode jsonNode = objectMapper.readTree(this.getClass().getResource("/mock/addLabels.json"));
        assertNotNull(jsonNode);
        final JsonNode reply = gitHubServiceBackend.addLabels(jsonNode);
        assertNotNull(reply);
        assertNotNull(reply.get("labels"));
    }

    @Test
    void addReviewers() throws IOException {
        final JsonNode jsonNode = objectMapper.readTree(this.getClass().getResource("/mock/addReviewers.json"));
        assertNotNull(jsonNode);
        final JsonNode reply = gitHubServiceBackend.addReviewers(jsonNode);
        assertNotNull(reply);
        assertNotNull(reply.get("reviewers"));
    }

    @Test
    void fetchPRFiles() throws IOException {
        final JsonNode jsonNode = objectMapper.readTree(this.getClass().getResource("/mock/addReviewers.json"));
        assertNotNull(jsonNode);
        final JsonNode reply = gitHubServiceBackend.fetchPRFiles(jsonNode);
        assertNotNull(reply);
        assertNotNull(reply.get("reviewers"));
        assertNotNull(reply.get("files"));
    }
}