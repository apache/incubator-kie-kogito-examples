/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.acme;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener to write to the default logger the lifecycle changes of the workflow states.
 */
@ApplicationScoped
public class WorkflowPrinterListenerConfig extends DefaultProcessEventListenerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowPrinterListenerConfig.class);

    public WorkflowPrinterListenerConfig() {
    }

    @PostConstruct
    public void setup() {
        register(new DefaultKogitoProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                LOGGER.info("Starting workflow {}", getProcessIdentifier(event));
            }

            public void afterProcessStarted(ProcessStartedEvent event) {
                LOGGER.info("Workflow {} was started, now {}", getProcessIdentifier(event), getStatus(event.getProcessInstance().getState()));
            }

            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                String nodeName = event.getNodeInstance().getNodeName();
                if (!"EmbeddedStart".equals(nodeName) && !"EmbeddedEnd".equals(nodeName) && !"Script".equals(nodeName)) {
                    LOGGER.info("Triggered node {} ({}) for process {}. Workflow data is {} ", nodeName, ((KogitoNodeInstance) event.getNodeInstance()).getStringId(),
                            event.getProcessInstance().getProcessId(), event.getNodeInstance().getVariable("workflowdata"));
                }
            }

            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                LOGGER.info("Data changed: {}", event.getNewValue());
            }

            private String getProcessIdentifier(ProcessStartedEvent event) {
                return String.format("%s (%s)", event.getProcessInstance().getProcessId(), ((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            }

            private String getStatus(int status) {
                switch (status) {
                    case 0:
                        return "PENDING";
                    case 1:
                        return "ACTIVE";
                    case 2:
                        return "COMPLETED";
                    case 3:
                        return "ABORTED";
                    case 4:
                        return "SUSPENDED";
                    default:
                        return "UNKNOWN " + status;
                }
            }
        });
    }

}
