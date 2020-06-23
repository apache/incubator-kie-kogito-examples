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
package org.kie.kogito.testcontainers;

import org.kie.kogito.resources.ConditionHolder;
import org.kie.kogito.resources.ConditionalTestResource;
import org.testcontainers.containers.GenericContainer;

/**
 * Container to be run if and only if it was enabled.
 */
public abstract class ConditionalGenericContainer<SELF extends GenericContainer<SELF>> extends GenericContainer<SELF> implements ConditionalTestResource<GenericContainer<SELF>> {

    private final ConditionHolder condition = new ConditionHolder(getResourceName());

    @Override
    public void start() {
        if (condition.isEnabled()) {
            preStart();
            super.start();
        }
    }

    @Override
    public void stop() {
        if (condition.isEnabled()) {
            super.stop();
        }
    }

    @Override
    public GenericContainer<SELF> enableConditional() {
        condition.enableConditional();
        return this;
    }

    protected abstract String getResourceName();

    protected void preStart() {

    }
}
