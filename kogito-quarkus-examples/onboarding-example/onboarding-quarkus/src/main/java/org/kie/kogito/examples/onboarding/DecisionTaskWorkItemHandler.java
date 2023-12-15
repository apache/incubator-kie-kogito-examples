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

import org.kie.kogito.addons.quarkus.k8s.workitems.QuarkusDiscoveredEndpointCaller;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;

import jakarta.ws.rs.HttpMethod;

public class DecisionTaskWorkItemHandler implements KogitoWorkItemHandler {

    private QuarkusDiscoveredEndpointCaller endpointCaller;

    public DecisionTaskWorkItemHandler(QuarkusDiscoveredEndpointCaller endpointCaller) {
        this.endpointCaller = endpointCaller;
    }

    @Override
    public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        Map<String, Object> results = endpointCaller.discoverAndCall(workItem, System.getenv("NAMESPACE"), "Decision", HttpMethod.POST);
        manager.completeWorkItem(workItem.getStringId(), results);
    }

    @Override
    public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {

    }

    @Override
    public String getName() {
        return "DecisionTask";
    }
}
