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
package org.kie.kogito.local;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import org.kie.kogito.resources.ConditionHolder;
import org.kie.kogito.resources.ConditionalTestResource;

public class LocalProcessTestResource implements ConditionalTestResource<LocalProcessTestResource> {

    private URL resource;
    private Process process;
    private final ConditionHolder condition;
    private String jvmArguments;

    public LocalProcessTestResource(String path, String jvmArguments) {
        this.resource = this.getClass().getClassLoader().getResource(path);
        this.condition = new ConditionHolder(path);
        this.jvmArguments = jvmArguments;
    }

    @Override
    public void start() {
        if (!condition.isEnabled()) {
            return;
        }

        String separator = System.getProperty("file.separator");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
        ProcessBuilder processBuilder = new ProcessBuilder(path,
                                                           jvmArguments,
                                                           "-jar", resource.getPath());
        try {
            this.process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        Optional.ofNullable(process).ifPresent(Process::destroy);
    }

    @Override
    public LocalProcessTestResource enableConditional() {
        condition.enableConditional();
        return this;
    }
}