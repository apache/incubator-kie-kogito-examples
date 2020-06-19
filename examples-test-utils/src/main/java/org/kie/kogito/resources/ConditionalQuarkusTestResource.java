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
package org.kie.kogito.resources;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * Quarkus resource to be run if and only if it was enabled.
 */
public abstract class ConditionalQuarkusTestResource implements QuarkusTestResourceLifecycleManager {
    private static final String ENABLE_PROPERTY = "enableIfTestCategoryIs";

    private final ConditionalTestResource<?> conditionalResource;
    
    public ConditionalQuarkusTestResource(ConditionalTestResource<?> conditionalResource) {
        this.conditionalResource = conditionalResource;
    }

    @Override
    public void init(Map<String, String> initArgs) {
        Optional.ofNullable(initArgs.get(ENABLE_PROPERTY)).ifPresent(conditionalResource::enableIfTestCategoryIs);
    }

    @Override
    public Map<String, String> start() {
        conditionalResource.start();
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        conditionalResource.stop();
    }
}
