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

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * KogitoApp GIT source configuration.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GitSource {

    private String uri;
    private String reference;
    private String contextDir;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getContextDir() {
        return contextDir;
    }

    public void setContextDir(String contextDir) {
        this.contextDir = contextDir;
    }
}
