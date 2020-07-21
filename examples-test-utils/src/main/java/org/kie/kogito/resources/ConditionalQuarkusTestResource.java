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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * Quarkus resource to be run if and only if it was enabled.
 */
public abstract class ConditionalQuarkusTestResource implements QuarkusTestResourceLifecycleManager {

    private final TestResource testResource;
    private final ConditionHolder condition;
    
    public ConditionalQuarkusTestResource(TestResource testResource) {
        this.testResource = testResource;
        this.condition = new ConditionHolder(testResource.getResourceName());
    }

    @Override
    public Map<String, String> start() {
        if (condition.isEnabled()) {
            testResource.start();
            return Collections.singletonMap(getKogitoProperty(), getKogitoPropertyValue());
        }

        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        if (condition.isEnabled()) {
            testResource.stop();
        }
    }

    @Override
    public void inject(Object testInstance) {
        Class<?> c = testInstance.getClass();
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                ConfigProperty configProperty = f.getAnnotation(ConfigProperty.class);
                if (configProperty != null && getKogitoProperty().equals(configProperty.name())) {
                    f.setAccessible(true);
                    try {
                        f.set(testInstance, getKogitoPropertyValue());
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            c = c.getSuperclass();
        }
    }

    protected abstract String getKogitoProperty();

    protected String getKogitoPropertyValue() {
        return "localhost:" + testResource.getMappedPort();
    }

    protected TestResource getTestResource() {
        return testResource;
    }

    protected void enableConditional() {
        condition.enableConditional();
    }
}
