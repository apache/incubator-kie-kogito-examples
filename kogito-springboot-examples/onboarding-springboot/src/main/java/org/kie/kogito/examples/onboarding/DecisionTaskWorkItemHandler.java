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
package org.kie.kogito.examples.onboarding;

import java.util.Map;
import java.util.Optional;

import org.kie.kogito.addons.springboot.k8s.workitems.SpringDiscoveredEndpointCaller;
import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemManager;
import org.kie.kogito.internal.process.workitem.WorkItemTransition;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;
import org.springframework.http.HttpMethod;

public class DecisionTaskWorkItemHandler extends DefaultKogitoWorkItemHandler {

    private SpringDiscoveredEndpointCaller endpointCaller;

    public DecisionTaskWorkItemHandler(SpringDiscoveredEndpointCaller endpointCaller) {
        this.endpointCaller = endpointCaller;
    }

    @Override
    public Optional<WorkItemTransition> activateWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        Map<String, Object> results = this.endpointCaller.discoverAndCall(workItem, System.getenv("NAMESPACE"), "Decision", HttpMethod.POST.toString());
        return Optional.of(handler.completeTransition(workItem.getPhaseStatus(), results));
    }

    @Override
    public String getName() {
        return "DecisionTask";
    }
}
