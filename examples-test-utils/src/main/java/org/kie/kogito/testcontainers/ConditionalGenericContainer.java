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

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.testcontainers.containers.GenericContainer;

/**
 * Container to be run if and only if it was enabled.
 */
public abstract class ConditionalGenericContainer<SELF extends GenericContainer<SELF>> extends GenericContainer<SELF> {

    private static final String TEST_CATEGORY_PROPERTY = "tests.category";

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void start() {
        if (isEnabled()) {
            preStart();
            super.start();
        }
    }

    @Override
    public void stop() {
        if (isEnabled()) {
            super.stop();
        }
    }

    public GenericContainer<SELF> enableIfTestCategoryIs(String value) {
        return enableIfSystemPropertyIs(TEST_CATEGORY_PROPERTY, value);
    }

    public GenericContainer<SELF> enableIfSystemPropertyIs(String name, String value) {
        this.enabled = Optional.ofNullable(System.getProperty(name)).map(property -> StringUtils.equalsIgnoreCase(property, value)).orElse(false);

        return this;
    }

    protected void preStart() {

    }
}
