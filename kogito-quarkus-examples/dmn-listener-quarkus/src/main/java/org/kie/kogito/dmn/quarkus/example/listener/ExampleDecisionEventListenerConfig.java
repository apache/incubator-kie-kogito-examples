/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.dmn.quarkus.example.listener;

import org.kie.kogito.dmn.config.CachedDecisionEventListenerConfig;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * This class demonstrates one of the two methods offered by Kogito to inject custom
 * {@link org.kie.dmn.api.core.event.DMNRuntimeEventListener}s in its internal {@link org.kie.dmn.api.core.DMNRuntime}.
 * <p>
 * It works by creating a bean that implements {@link org.kie.kogito.decision.DecisionEventListenerConfig} interface
 * (which returns the list of desired instances in the {@link org.kie.kogito.decision.DecisionEventListenerConfig#listeners()}
 * method) and annotating it with {@link ApplicationScoped}.
 * <p>
 * We're extending {@link CachedDecisionEventListenerConfig} instead of implementing the interface directly
 * only because the intermediate class provides a utility method to register listener instances. This is the
 * suggested way if the Config class is not supposed to contain any extra logic.
 * <p>
 * The second injection method is explained in {@link ExampleDMNRuntimeEventListener}.
 * All the listeners instantiated with both methods will be injected during the application startup phase.
 */
@ApplicationScoped
public class ExampleDecisionEventListenerConfig extends CachedDecisionEventListenerConfig {

    public ExampleDecisionEventListenerConfig() {
        register(new LoggingDMNRuntimeEventListener("ExampleDecisionEventListenerConfig's inner listener #1"));
        register(new LoggingDMNRuntimeEventListener("ExampleDecisionEventListenerConfig's inner listener #2"));
    }

}
