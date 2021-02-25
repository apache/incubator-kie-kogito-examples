/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.examples.onboarding;

import java.util.Map;

import org.kie.kogito.cloud.workitems.DiscoveredServiceWorkItemHandler;
import org.kie.kogito.cloud.workitems.HttpMethods;
import org.kie.kogito.cloud.workitems.ServiceInfo;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;

public class DecisionTaskWorkItemHandler extends DiscoveredServiceWorkItemHandler {

    public DecisionTaskWorkItemHandler() {
        if ("true".equalsIgnoreCase(System.getProperty("local"))) {
            this.addServices("id", new ServiceInfo("http://localhost:8081/id", null));
            this.addServices("department", new ServiceInfo("http://localhost:8081/department/first", null));
            this.addServices("employeeValidation", new ServiceInfo("http://localhost:8081/employee-validation/first", null));
            this.addServices("vacations/days", new ServiceInfo("http://localhost:8082/vacations/days", null));
            this.addServices("taxes/rate", new ServiceInfo("http://localhost:8082/taxes/rate", null));
            this.addServices("payments/date", new ServiceInfo("http://localhost:8082/payments/date", null));
        }
    }

    @Override
    public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        Map<String, Object> results = discoverAndCall(workItem, System.getenv("NAMESPACE"), "Decision", HttpMethods.POST);

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
