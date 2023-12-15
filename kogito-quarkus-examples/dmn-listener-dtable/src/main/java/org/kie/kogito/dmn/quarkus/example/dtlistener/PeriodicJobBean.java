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

import java.util.concurrent.TimeUnit;

import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.kogito.decision.DecisionModels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.scheduler.Scheduled;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * This example periodic Job uses a pre-identified DMN model coordinates.
 * Alternatively:
 * - use `@Inject DecisionModelResourcesProvider provider`
 * - ensure `org.kie.kogito:kogito-addons-quarkus-tracing-decision` is added to the POM
 * - then use provider.get() to cycle programmatically on all known DMN model coordinates.
 */
@ApplicationScoped
public class PeriodicJobBean {
    private static final Logger LOG = LoggerFactory.getLogger(PeriodicJobBean.class);

    @Inject
    ExampleDMNRuntimeEventListener listener;

    @Inject
    DecisionModels models; // can't use yet Incubation API here as I need access to DMNModel

    @Scheduled(every = "5s")
    public void logEvents() {
        for (AfterEvaluateDecisionTableEvent event : listener.getEvents()) {
            DecisionTable dt = models.getDecisionModel("myNS", "dtevent").getDMNModel().getDefinitions()
                    .findAllChildren(DecisionTable.class).stream()
                    .filter(t -> t.getId().equals(event.getDecisionTableId()))
                    .findFirst().orElseThrow(IllegalStateException::new); // only one having that ID, checked during build time by kie-dmn-validation
            LOG.info("Decision Table ({}) having a total of {} rows, during evaluation matched rows: {}, and selected rows: {}.",
                    event.getDecisionTableId(),
                    dt.getRule().size(), // from the original DT definition
                    event.getMatches(), // which rules have matched during evaluation
                    event.getSelected()); // which rules have been selected from matching set, by hit policy
        }
    }

    @Scheduled(every = "60s", delay = 60, delayUnit = TimeUnit.SECONDS)
    public void clearEvents() {
        LOG.info("Periodically clearing event logs.");
        listener.getEvents().clear();
    }
}
