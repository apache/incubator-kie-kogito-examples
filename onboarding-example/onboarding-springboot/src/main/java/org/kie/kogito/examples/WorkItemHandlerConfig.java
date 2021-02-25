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
package org.kie.kogito.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.kogito.examples.onboarding.DecisionTaskWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;
import org.springframework.stereotype.Component;

@Component
public class WorkItemHandlerConfig extends DefaultWorkItemHandlerConfig {

    private final Map<String, KogitoWorkItemHandler> workItemHandlers = new HashMap<>();
    private final List<String> supportedHandlers = Arrays.asList("AssignDepartmentAndManager",
            "CalculatePaymentDate",
            "CalculateVacationDays",
            "CalculateTaxRate",
            "ValidateEmployee",
            "AssignIdAndEmail",
            "DecisionTask");

    @Override
    public KogitoWorkItemHandler forName(String name) {

        workItemHandlers.putIfAbsent("DecisionTask", new DecisionTaskWorkItemHandler());
        if (supportedHandlers.contains(name)) {
            // use decision task handler (single instance) for all supported handlers that are based on decision calls
            return workItemHandlers.get("DecisionTask");
        }

        return super.forName(name);
    }

    @Override
    public Collection<String> names() {
        List<String> names = new ArrayList<>(supportedHandlers);
        names.addAll(super.names());
        return names;
    }
}
