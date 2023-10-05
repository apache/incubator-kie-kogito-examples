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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kohsuke.github.GHPullRequestFileDetail;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GitHubWrapperServiceImpl implements GitHubWrapperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubWrapperServiceImpl.class);

    @Inject
    TokenProvider tokenProvider;

    public GitHubWrapperServiceImpl() {
    }

    public void addLabels(String user, String repository, int issueId, List<String> labels) throws Exception {
        LOGGER.info("Adding labels for the repo {}/{} issue {} labels {}", user, repository, issueId, labels);
        final GitHub gitHub = new GitHubBuilder().withAppInstallationToken(tokenProvider.getToken()).build();
        gitHub.getRepository(toRepositoryName(user, repository)).getIssue(issueId).addLabels(labels.toArray(new String[] {}));
        LOGGER.info("Labels {} added to the Issue/PR {}", labels, issueId);
    }

    public void addReviewers(String user, String repository, int prId, List<String> reviewers) throws Exception {
        LOGGER.info("Adding reviewers for the repo {}/{} PR {} labels {}", user, repository, prId, reviewers);
        final GitHub gitHub = new GitHubBuilder().withAppInstallationToken(tokenProvider.getToken()).build();
        gitHub
                .getRepository(toRepositoryName(user, repository))
                .getPullRequest(prId)
                .requestReviewers(toUsers(gitHub, reviewers));
        LOGGER.info("Reviewers {} added to the PR {}", reviewers, prId);
    }

    public List<String> fetchChangedFilesPath(String user, String repository, int prId) throws Exception {
        LOGGER.info("Fetching files for the repo {}/{} PR {}", user, repository, prId);
        final List<String> filesPath = new ArrayList<>();
        final GitHub gitHub = new GitHubBuilder().withAppInstallationToken(tokenProvider.getToken()).build();
        for (GHPullRequestFileDetail ghPullRequestFileDetail : gitHub
                .getRepository(toRepositoryName(user, repository))
                .getPullRequest(prId)
                .listFiles()) {
            filesPath.add(ghPullRequestFileDetail.getFilename());
        }
        LOGGER.info("Fetched files {} for PR {}", filesPath, prId);
        return filesPath;
    }

    private String toRepositoryName(String user, String repository) {
        return user.concat("/").concat(repository);
    }

    private List<GHUser> toUsers(final GitHub gitHub, final List<String> userIds) throws IOException {
        final List<GHUser> users = new ArrayList<>();
        if (userIds != null) {
            for (String u : userIds) {
                users.add(gitHub.getUser(u));
            }
        }
        return users;
    }
}
