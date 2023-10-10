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
package org.kie.kogito.examples.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class RecordedWorkItemHandlerConfig extends DefaultWorkItemHandlerConfig {

    private RecordedOutputWorkItemHandler handler = new RecordedOutputWorkItemHandler();
    private final List<String> supportedHandlers = Arrays.asList("AssignDepartmentAndManager",
            "CalculatePaymentDate",
            "CalculateVacationDays",
            "CalculateTaxRate",
            "ValidateEmployee",
            "AssignIdAndEmail",
            "DecisionTask");

    @Override
    public KogitoWorkItemHandler forName(String name) {
        if (supportedHandlers.contains(name)) {
            return handler;
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
