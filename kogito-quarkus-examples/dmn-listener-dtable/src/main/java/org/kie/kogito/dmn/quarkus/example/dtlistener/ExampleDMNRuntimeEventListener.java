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
package org.kie.kogito.dmn.quarkus.example.dtlistener;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.core.api.event.DefaultDMNRuntimeEventListener;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * This class demonstrates one possible use of the `AfterEvaluateDecisionTableEvent` asynchronously to the listener.
 */
@ApplicationScoped
public class ExampleDMNRuntimeEventListener extends DefaultDMNRuntimeEventListener {
    // using a ConcurrentLinkedQueue to avoid ConcurrentModificationException
    private Queue<AfterEvaluateDecisionTableEvent> events = new ConcurrentLinkedQueue<>();

    @Override
    public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
        // Maintaining listener callback code as small as possible.
        events.add(event);
    }

    public Queue<AfterEvaluateDecisionTableEvent> getEvents() {
        return events;
    }
}
