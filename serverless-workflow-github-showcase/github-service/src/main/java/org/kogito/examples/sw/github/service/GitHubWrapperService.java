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

package org.kogito.examples.sw.github.service;

import java.util.List;

public interface GitHubWrapperService {

    void addLabels(String user, String repository, int issueId, List<String> labels) throws Exception;

    void addReviewers(String user, String repository, int prId, List<String> reviewers) throws Exception;

    List<String> fetchChangedFilesPath(String user, String repository, int prId) throws Exception;
}
