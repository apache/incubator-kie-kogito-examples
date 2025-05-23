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
package org.kie.kogito.app;

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

import io.micrometer.prometheus.PrometheusMeterRegistry;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CustomDMNRuntimeEventListener implements DMNRuntimeEventListener {

    private static final Logger logger = LoggerFactory.getLogger(CustomDMNRuntimeEventListener.class);

    private final PrometheusMeterRegistry prometheusMeterRegistry;

    @Inject
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
        prometheusMeterRegistry.counter("org.kie.kogito.examples.customdmnruntimeeventlistener", "event",
                event.getClass().getSimpleName().toLowerCase()).increment();
    }

}
