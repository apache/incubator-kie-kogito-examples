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
package org.kie.kogito.testcontainers.quarkus;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.kie.kogito.testcontainers.InfinispanContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * Infinispan quarkus resource that works within the test lifecycle.
 *
 */
public class InfinispanQuarkusResource implements QuarkusTestResourceLifecycleManager {

    private static final String ENABLE_PROPERTY = "enableIfTestCategoryIs";

    private final InfinispanContainer infinispan = new InfinispanContainer();

    @Override
    public void init(Map<String, String> initArgs) {
        Optional.ofNullable(initArgs.get(ENABLE_PROPERTY)).ifPresent(infinispan::enableIfTestCategoryIs);
    }

    @Override
    public Map<String, String> start() {
        infinispan.start();
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        infinispan.stop();
    }

}
