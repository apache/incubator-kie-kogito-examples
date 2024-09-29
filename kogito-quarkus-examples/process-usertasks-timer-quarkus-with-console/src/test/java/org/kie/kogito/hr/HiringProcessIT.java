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
package org.kie.kogito.hr;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.testcontainers.quarkus.InfinispanQuarkusTestResource;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.UserTasks;
import org.kie.kogito.usertask.impl.lifecycle.DefaultUserTaskLifeCycle;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(value = InfinispanQuarkusTestResource.class)
@QuarkusTestResource(value = KafkaQuarkusTestResource.class)
public class HiringProcessIT {

    @Named("hiring")
    @Inject
    Process<? extends Model> hiringProcess;

    @Inject
    UserTasks userTasks;

    @Test
    public void testApprovalProcess() {

        assertNotNull(hiringProcess);

        Model m = hiringProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("candidate", new Candidate("jdoe", "jdoe@example.com", 30000, "Java, Kogito"));
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = hiringProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());

        IdentityProvider jdoeUser = IdentityProviders.of("jdoe", Arrays.asList("HR", "IT"));
        SecurityPolicy policy = SecurityPolicy.of(jdoeUser);

        processInstance.workItems(policy);

        List<WorkItem> workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());

        List<UserTaskInstance> userTaskInstances = userTasks.instances().findByIdentity(IdentityProviders.of("jdoe"));
        userTaskInstances.forEach(ut -> {
            ut.setOutput("approve", true);
            ut.transition(DefaultUserTaskLifeCycle.COMPLETE, Collections.emptyMap(), IdentityProviders.of("jdoe"));
        });

        processInstance.workItems(policy);

        workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());

        userTaskInstances = userTasks.instances().findByIdentity(IdentityProviders.of("jdoe"));
        userTaskInstances.forEach(ut -> {
            ut.setOutput("approve", false);
            ut.transition(DefaultUserTaskLifeCycle.COMPLETE, Collections.emptyMap(), IdentityProviders.of("jdoe"));
        });

        // process does not exist anymore
        assertThat(hiringProcess.instances().findById(processInstance.id())).isEmpty();

    }
}
