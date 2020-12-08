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
import java.nio.charset.Charset;

import com.github.jknack.handlebars.internal.Files;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(GitHubServiceMockServer.class) // mock the GitHub API
@QuarkusTestResource(MessageSinkServer.class) // mock the Knative Eventing Broker
class PRCheckerWorkflowTest {

    final static Logger LOGGER = LoggerFactory.getLogger(PRCheckerWorkflowTest.class);

    @Test
    void onPREdited() throws IOException {
        final String pullRequestEvent = Files.read(this.getClass().getResourceAsStream("/mock/ce_pr_edited.json"), Charset.defaultCharset());
        assertNotNull(pullRequestEvent);
        LOGGER.debug("CE read as {}", pullRequestEvent);

        given()
                .contentType(JsonFormat.CONTENT_TYPE)
                .body(pullRequestEvent).post("/").then().statusCode(200);
    }
}
