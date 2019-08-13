/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kogito.examples.openshift.operator.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * KogitoApp build configuration.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Build {

    private Boolean incremental;
    private GitSource gitSource;
    private List<Env> env = new ArrayList<>();

    public Boolean getIncremental() {
        return incremental;
    }

    public void setIncremental(Boolean incremental) {
        this.incremental = incremental;
    }

    public GitSource getGitSource() {
        return gitSource;
    }

    public void setGitSource(GitSource gitSource) {
        this.gitSource = gitSource;
    }

    public void addEnv(Env env) {
        this.env.add(env);
    }

    public void addEnvs(List<Env> envs) {
        this.env.addAll(envs);
    }

    public Env[] getEnv() {
        return env.toArray(new Env[0]);
    }

    public void setEnv(Env[] env) {
        this.env = Arrays.asList(env);
    }
}
