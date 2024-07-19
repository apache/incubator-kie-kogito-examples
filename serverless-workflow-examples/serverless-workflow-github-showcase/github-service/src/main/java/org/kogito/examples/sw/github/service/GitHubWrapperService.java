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
package org.kogito.examples.sw.github.service;

import java.util.List;

public interface GitHubWrapperService {

    /**
     * Adds labels to the given issue (can also be a PR in GitHub context)
     *
     * @param user owner of the repository
     * @param repository name of the repository
     * @param issueId identification of the issue
     * @param labels list with the labels to add
     * @throws Exception in case something goes wrong
     */
    void addLabels(String user, String repository, int issueId, List<String> labels) throws Exception;

    /**
     * Adds reviewers to the given PR
     *
     * @param user owner of the repository
     * @param repository name of the repository
     * @param prId identification of the PR
     * @param reviewers list with the reviewers to be added
     * @throws Exception in case something goes wrong
     */
    void addReviewers(String user, String repository, int prId, List<String> reviewers) throws Exception;

    /**
     * Fetches the files changed in a given PR
     *
     * @param user owner of the repository
     * @param repository name of the repository
     * @param prId identification of the PR
     * @return list of files changed in this PR
     * @throws Exception in case something goes wrong
     */
    List<String> fetchChangedFilesPath(String user, String repository, int prId) throws Exception;
}
