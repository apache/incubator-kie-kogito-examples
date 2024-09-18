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

package org.acme.travels.usertasks;

import java.util.Optional;

import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemManager;
import org.kie.kogito.internal.process.workitem.WorkItemLifeCycle;
import org.kie.kogito.internal.process.workitem.WorkItemPhaseState;
import org.kie.kogito.internal.process.workitem.WorkItemTerminationType;
import org.kie.kogito.internal.process.workitem.WorkItemTransition;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;
import org.kie.kogito.process.workitems.impl.DefaultWorkItemLifeCycle;
import org.kie.kogito.process.workitems.impl.DefaultWorkItemLifeCyclePhase;

public class CustomHumanTaskWorkItemHandler extends DefaultKogitoWorkItemHandler {

    public static final String TRANSITION_COMPLETE = "complete";
    public static final String TRANSITION_ABORT = "abort";
    public static final String TRANSITION_ACTIVATE = "activate";
    public static final String TRANSITION_START = "start";
    public static final String TRANSITION_SKIP = "skip";

    @Override
    public WorkItemLifeCycle initialize() {
        WorkItemPhaseState initialized = WorkItemPhaseState.initialized();
        WorkItemPhaseState completed = WorkItemPhaseState.of("Completed", WorkItemTerminationType.COMPLETE);
        WorkItemPhaseState aborted = WorkItemPhaseState.of("Aborted", WorkItemTerminationType.ABORT);
        WorkItemPhaseState activated = WorkItemPhaseState.of("Activated");
        WorkItemPhaseState started = WorkItemPhaseState.of("Started");

        DefaultWorkItemLifeCyclePhase active = new DefaultWorkItemLifeCyclePhase(TRANSITION_ACTIVATE, initialized, activated, this::activateWorkItemHandler);
        DefaultWorkItemLifeCyclePhase start = new DefaultWorkItemLifeCyclePhase(TRANSITION_START, activated, started, this::activateWorkItemHandler);
        DefaultWorkItemLifeCyclePhase complete = new DefaultWorkItemLifeCyclePhase(TRANSITION_COMPLETE, started, completed, this::completeWorkItemHandler);
        DefaultWorkItemLifeCyclePhase abort = new DefaultWorkItemLifeCyclePhase(TRANSITION_ABORT, started, aborted, this::abortWorkItemHandler);

        return new DefaultWorkItemLifeCycle(active, start, abort, complete);
    }

    @Override
    public Optional<WorkItemTransition> completeWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workitem, WorkItemTransition transition) {
        getUserFromTransition(transition).ifPresent(e -> workitem.setOutput("ActorId", e));
        return Optional.empty();
    }

    private Optional<String> getUserFromTransition(WorkItemTransition transition) {
        Optional<SecurityPolicy> securityPolicy = transition.policies().stream().filter(SecurityPolicy.class::isInstance).map(SecurityPolicy.class::cast).findAny();
        if (securityPolicy.isPresent()) {
            return Optional.ofNullable(securityPolicy.get().getUser());
        }
        return Optional.empty();
    }
}
