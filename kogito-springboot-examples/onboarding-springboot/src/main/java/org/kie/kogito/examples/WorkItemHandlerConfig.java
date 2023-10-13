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
package org.kie.kogito.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.kie.kogito.addons.k8s.Endpoint;
import org.kie.kogito.addons.k8s.EndpointQueryKey;
import org.kie.kogito.addons.k8s.LocalEndpointDiscovery;
import org.kie.kogito.addons.springboot.k8s.workitems.SpringDiscoveredEndpointCaller;
import org.kie.kogito.examples.onboarding.DecisionTaskWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WorkItemHandlerConfig extends DefaultWorkItemHandlerConfig {

    @Autowired
    private SpringDiscoveredEndpointCaller endpointCaller;

    @Value("${org.acme.kogito.onboarding.local}")
    Boolean isLocalRunning;

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
        workItemHandlers.putIfAbsent("DecisionTask", new DecisionTaskWorkItemHandler(this.endpointCaller));
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

    @PostConstruct
    public void loadLocalServicesIfNotOnKube() {
        if (isLocalRunning) {
            final LocalEndpointDiscovery endpointDiscovery = new LocalEndpointDiscovery();
            final String namespace = System.getenv("NAMESPACE");
            endpointDiscovery.addCache(new EndpointQueryKey(namespace, "id"), new Endpoint("http://localhost:8081/id"));
            endpointDiscovery.addCache(new EndpointQueryKey(namespace, "department"), new Endpoint("http://localhost:8081/department/first"));
            endpointDiscovery.addCache(new EndpointQueryKey(namespace, "employeeValidation"), new Endpoint("http://localhost:8081/employee-validation/first"));
            endpointDiscovery.addCache(new EndpointQueryKey(namespace, "vacations/days"), new Endpoint("http://localhost:8082/vacations/days"));
            endpointDiscovery.addCache(new EndpointQueryKey(namespace, "taxes/rate"), new Endpoint("http://localhost:8082/taxes/rate"));
            endpointDiscovery.addCache(new EndpointQueryKey(namespace, "payments/date"), new Endpoint("http://localhost:8082/payments/date"));
            this.endpointCaller.setEndpointDiscovery(endpointDiscovery);
        }
    }
}
