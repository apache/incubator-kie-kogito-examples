/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.acme.app;

import org.kie.dmn.api.core.event.AfterEvaluateAllEvent;
import org.kie.dmn.api.core.event.AfterEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateAllEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import io.micrometer.prometheus.PrometheusMeterRegistry;

@Configuration
public class CustomDMNRuntimeEventListener implements DMNRuntimeEventListener {

    private static final Logger logger = LoggerFactory.getLogger(CustomDMNRuntimeEventListener.class);

    private final PrometheusMeterRegistry prometheusMeterRegistry;

    @Autowired
    public CustomDMNRuntimeEventListener(PrometheusMeterRegistry prometheusMeterRegistry) {
        this.prometheusMeterRegistry = prometheusMeterRegistry;
    }

    @Override
    public void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {
        registerEvent(event);
    }

    @Override
    public void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {
        registerEvent(event);
    }

    @Override
    public void beforeEvaluateContextEntry(BeforeEvaluateContextEntryEvent event) {
        registerEvent(event);
    }

    @Override
    public void afterEvaluateContextEntry(AfterEvaluateContextEntryEvent event) {
        registerEvent(event);
    }

    @Override
    public void beforeEvaluateDecisionTable(BeforeEvaluateDecisionTableEvent event) {
        registerEvent(event);
    }

    @Override
    public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
        registerEvent(event);
    }

    @Override
    public void beforeEvaluateAll(BeforeEvaluateAllEvent event) {
        registerEvent(event);
    }

    @Override
    public void afterEvaluateAll(AfterEvaluateAllEvent event) {
        registerEvent(event);
    }

    private void registerEvent(DMNEvent event) {
        logger.debug(event.getClass().getSimpleName());
        prometheusMeterRegistry.counter("org.acme.examples.customdmnruntimeeventlistener", "event",
                event.getClass().getSimpleName().toLowerCase()).increment();
    }
}
