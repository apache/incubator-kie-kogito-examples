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
package org.acme.travels.custom.lifecycle.quarkus;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acme.travels.Address;
import org.acme.travels.Traveller;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.internal.process.workitem.InvalidTransitionException;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;

import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class ApprovalsProcessTest {

    @Named("approvals")
    @Inject
    Process<? extends Model> approvalsProcess;

    @Test
    public void testApprovalProcess() {

        assertNotNull(approvalsProcess);

        Model m = approvalsProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("traveller", new Traveller("John", "Doe", "john.doe@example.com", "American", new Address("main street", "Boston", "10005", "US")));
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = approvalsProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());

        IdentityProvider identity = IdentityProviders.of("admin", Collections.singletonList("managers"));
        SecurityPolicy policy = SecurityPolicy.of(identity);

        processInstance.workItems(policy);

        List<WorkItem> workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());
        Map<String, Object> results = new HashMap<>();
        results.put("approved", true);
        KogitoWorkItemHandler handler = approvalsProcess.getKogitoWorkItemHandler(workItems.get(0).getWorkItemHandlerName());
        processInstance.transitionWorkItem(workItems.get(0).getId(), handler.newTransition("start", workItems.get(0).getPhaseStatus(), Collections.emptyMap(), policy));
        workItems = processInstance.workItems(policy);
        processInstance.completeWorkItem(workItems.get(0).getId(), results, policy);

        policy = SecurityPolicy.of(IdentityProviders.of("admin", singletonList("mgmt")));
        workItems = processInstance.workItems(policy);
        assertEquals(0, workItems.size());

        identity = IdentityProviders.of("john", Collections.singletonList("managers"));
        policy = SecurityPolicy.of(identity);

        processInstance.workItems(policy);

        workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());

        results.put("approved", false);
        processInstance.transitionWorkItem(workItems.get(0).getId(), handler.newTransition("start", workItems.get(0).getPhaseStatus(), Collections.emptyMap(), policy));
        workItems = processInstance.workItems(policy);
        processInstance.completeWorkItem(workItems.get(0).getId(), results, policy);
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());

        Model result = (Model) processInstance.variables();
        assertEquals(4, result.toMap().size());
        assertEquals(result.toMap().get("approver"), "admin");
        assertEquals(result.toMap().get("firstLineApproval"), true);
        assertEquals(result.toMap().get("secondLineApproval"), false);
    }

    @Test
    public void testApprovalProcessViaPhases() {

        assertNotNull(approvalsProcess);

        Model m = approvalsProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("traveller", new Traveller("John", "Doe", "john.doe@example.com", "American", new Address("main street", "Boston", "10005", "US")));
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = approvalsProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());

        IdentityProvider identity = IdentityProviders.of("admin", Collections.singletonList("managers"));
        SecurityPolicy policy = SecurityPolicy.of(identity);

        processInstance.workItems(policy);

        List<WorkItem> workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());

        final String wiId = workItems.get(0).getId();

        // test to make sure you can't complete if the task is not in started state
        assertThrows(InvalidTransitionException.class, () -> processInstance.completeWorkItem(wiId,
                Collections.singletonMap("approved", true),
                SecurityPolicy.of(IdentityProviders.of("admin", Collections.singletonList("managers")))));

        // now test going through phases
        KogitoWorkItemHandler handler = approvalsProcess.getKogitoWorkItemHandler(workItems.get(0).getWorkItemHandlerName());
        processInstance.transitionWorkItem(workItems.get(0).getId(), handler.newTransition("start", workItems.get(0).getPhaseStatus(), Collections.emptyMap(), policy));
        workItems = processInstance.workItems(policy);
        processInstance.transitionWorkItem(workItems.get(0).getId(), handler.newTransition("complete", workItems.get(0).getPhaseStatus(), Collections.singletonMap("approved", true), policy));

        policy = SecurityPolicy.of(IdentityProviders.of("admin", singletonList("mgmt")));
        workItems = processInstance.workItems(policy);
        assertEquals(0, workItems.size());

        identity = IdentityProviders.of("john", Collections.singletonList("managers"));
        policy = SecurityPolicy.of(identity);

        processInstance.workItems(policy);

        workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());

        processInstance.transitionWorkItem(workItems.get(0).getId(), handler.newTransition("start", workItems.get(0).getPhaseStatus(), Collections.emptyMap(), policy));
        workItems = processInstance.workItems(policy);
        processInstance.transitionWorkItem(workItems.get(0).getId(), handler.newTransition("complete", workItems.get(0).getPhaseStatus(), Collections.singletonMap("approved", false), policy));

        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());

        Model result = (Model) processInstance.variables();
        assertEquals(4, result.toMap().size());
        assertEquals(result.toMap().get("approver"), "admin");
        assertEquals(result.toMap().get("firstLineApproval"), true);
        assertEquals(result.toMap().get("secondLineApproval"), false);
    }
}
