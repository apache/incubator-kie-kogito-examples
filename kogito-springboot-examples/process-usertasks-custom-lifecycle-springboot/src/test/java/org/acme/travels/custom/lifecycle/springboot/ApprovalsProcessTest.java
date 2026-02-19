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
package org.acme.travels.custom.lifecycle.springboot;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acme.travels.Address;
import org.acme.travels.Traveller;
import org.acme.travels.usertasks.CustomUserTaskLifeCycle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.Model;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.tests.KogitoApplication;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.UserTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class ApprovalsProcessTest {

    @Autowired
    @Qualifier("approvals")
    Process<? extends Model> approvalsProcess;

    @Autowired
    UserTasks userTasks;

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
        processInstance.completeWorkItem(workItems.get(0).getId(), results, policy);
        assertThat(approvalsProcess.instances().findById(processInstance.id())).isNotPresent();
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

        List<UserTaskInstance> userTaskInstances = userTasks.instances().findByIdentity(identity);
        assertThat(userTaskInstances).isNotEmpty();
        userTaskInstances.forEach(ut -> {
            IdentityProvider userIdentity = IdentityProviders.of("manager", Collections.singletonList("managers"));
            ut.transition(CustomUserTaskLifeCycle.START, Collections.emptyMap(), userIdentity);
            assertThat(ut.getStatus()).isEqualTo(CustomUserTaskLifeCycle.RESERVED);
            ut.setOutput("approved", true);
            ut.transition(CustomUserTaskLifeCycle.COMPLETE, Collections.emptyMap(), userIdentity);
        });

        policy = SecurityPolicy.of(IdentityProviders.of("admin", singletonList("mgmt")));
        workItems = processInstance.workItems(policy);
        assertEquals(0, workItems.size());

        identity = IdentityProviders.of("john", Collections.singletonList("managers"));
        policy = SecurityPolicy.of(identity);

        workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());

        userTaskInstances = userTasks.instances().findByIdentity(identity);
        assertThat(userTaskInstances).isNotEmpty();
        userTaskInstances.forEach(ut -> {
            IdentityProvider userIdentity = IdentityProviders.of("john", Collections.singletonList("managers"));
            assertThat(ut.getStatus()).isEqualTo(CustomUserTaskLifeCycle.ACTIVE);
            ut.transition(CustomUserTaskLifeCycle.START, Collections.emptyMap(), userIdentity);
            ut.transition(CustomUserTaskLifeCycle.CLAIM, Collections.emptyMap(), userIdentity);
            ut.setOutput("approved", false);
            ut.transition(CustomUserTaskLifeCycle.COMPLETE, Collections.emptyMap(), userIdentity);
        });

        assertThat(approvalsProcess.instances().findById(processInstance.id())).isNotPresent();

    }
}
