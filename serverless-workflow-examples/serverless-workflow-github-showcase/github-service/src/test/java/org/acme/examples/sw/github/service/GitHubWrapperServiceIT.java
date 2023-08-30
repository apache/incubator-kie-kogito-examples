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
package org.acme.examples.sw.github.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertFalse;

// This test is just to perform quick and dirty smoke tests against the API
// change the constants to your real repos if you need to give it a try
@Disabled
@QuarkusTest
class GitHubWrapperServiceIT {

    private static final String REPO_USER = "";
    private static final String REPO_NAME = "";
    private static final String REVIEWER = "";
    private static final int ISSUE_ID = 1;
    private static final int PR_ID = 2;

    @Inject
    GitHubWrapperServiceImpl service;

    @Test
    void simpleAddLabelsCheck() throws Exception {
        service.addLabels(REPO_USER, REPO_NAME, ISSUE_ID, Arrays.asList("bug", "documentation"));
    }

    @Test
    void simpleAddReviewersCheck() throws Exception {
        service.addReviewers(REPO_USER, REPO_NAME, PR_ID, Collections.singletonList(REVIEWER));
        service.addLabels(REPO_USER, REPO_NAME, ISSUE_ID, Collections.singletonList("bug"));
    }

    @Test
    void simpleFetchChangedFilesCheck() throws Exception {
        final List<String> files = service.fetchChangedFilesPath(REPO_USER, REPO_NAME, PR_ID);
        assertFalse(files.isEmpty());
    }
}
