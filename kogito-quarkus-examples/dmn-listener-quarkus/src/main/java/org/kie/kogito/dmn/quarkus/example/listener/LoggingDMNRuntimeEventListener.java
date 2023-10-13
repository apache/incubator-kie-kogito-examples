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

import org.kie.dmn.api.core.event.AfterEvaluateAllEvent;
import org.kie.dmn.api.core.event.AfterEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateAllEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple utility class that logs the name of received events, used as base class of
 * all the {@link DMNRuntimeEventListener}s instantiated in this example.
 */
class LoggingDMNRuntimeEventListener implements DMNRuntimeEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(DMNRuntimeEventListener.class);
    private final String name;

    public LoggingDMNRuntimeEventListener(String name) {
        this.name = name;
    }

    @Override
    public void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {
        log("BeforeEvaluateDecisionEvent");
    }

    @Override
    public void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {
        log("AfterEvaluateDecisionEvent");
    }

    @Override
    public void beforeEvaluateContextEntry(BeforeEvaluateContextEntryEvent event) {
        log("BeforeEvaluateContextEntryEvent");
    }

    @Override
    public void afterEvaluateContextEntry(AfterEvaluateContextEntryEvent event) {
        log("AfterEvaluateContextEntryEvent");
    }

    @Override
    public void beforeEvaluateDecisionTable(BeforeEvaluateDecisionTableEvent event) {
        log("BeforeEvaluateDecisionTableEvent");
    }

    @Override
    public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
        log("AfterEvaluateDecisionTableEvent");
    }

    @Override
    public void beforeEvaluateAll(BeforeEvaluateAllEvent event) {
        log("BeforeEvaluateAllEvent");
    }

    @Override
    public void afterEvaluateAll(AfterEvaluateAllEvent event) {
        log("AfterEvaluateAllEvent");
    }

    private void log(String event) {
        LOG.info("{} received by {}", event, name);
    }

}
