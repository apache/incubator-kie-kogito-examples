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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.examples.demo.Order;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.testcontainers.quarkus.InfinispanQuarkusTestResource;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abort;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertEmpty;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.getFirst;

@QuarkusTest
@QuarkusTestResource(value = InfinispanQuarkusTestResource.Conditional.class)
@QuarkusTestResource(value = KafkaQuarkusTestResource.Conditional.class)
public class OrdersProcessIT {

    @Inject
    @Named("demo.orders")
    Process<? extends Model> orderProcess;

    @Inject
    @Named("demo.orderItems")
    Process<? extends Model> orderItemsProcess;

    private SecurityPolicy policy = SecurityPolicy.of(IdentityProviders.of("john", Collections.singletonList("managers")));

    @BeforeEach
    public void setup() {
        // abort all instances after each test
        // as other tests might have added instances
        // needed until Quarkus implements @DirtiesContext similar to springboot
        // see https://github.com/quarkusio/quarkus/pull/2866
        abort(orderProcess.instances());
        abort(orderItemsProcess.instances());
    }

    @Test
    public void testOrderProcess() {
        assertNotNull(orderProcess);

        Model m = orderProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("approver", "john");
        parameters.put("order", new Order("12345", false, 0.0));
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = orderProcess.createInstance(m);
        processInstance.start();

        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.status());
        Model result = (Model) processInstance.variables();
        assertEquals(2, result.toMap().size());
        assertTrue(((Order) result.toMap().get("order")).getTotal() > 0);

        ProcessInstances<? extends Model> orderItemProcesses = orderItemsProcess.instances();

        ProcessInstance<?> childProcessInstance = getFirst(orderItemProcesses);

        List<WorkItem> workItems = childProcessInstance.workItems(policy);
        assertEquals(1, workItems.size());
        childProcessInstance.completeWorkItem(workItems.get(0).getId(), null, policy);

        assertEquals(ProcessInstance.STATE_COMPLETED, childProcessInstance.status());
        Optional<?> pi = orderProcess.instances().findById(processInstance.id());
        assertFalse(pi.isPresent());

        assertEmpty(orderProcess.instances());
        assertEmpty(orderItemsProcess.instances());
    }

    @Test
    public void testOrderProcessWithError() {
        assertNotNull(orderProcess);

        Model m = orderProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("order", new Order("12345", false, 0.0));
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = orderProcess.createInstance(m);
        processInstance.start();

        assertEquals(ProcessInstance.STATE_ERROR, processInstance.status());
        assertTrue(processInstance.error().isPresent());

        parameters = new HashMap<>();
        parameters.put("approver", "john");
        parameters.put("order", new Order("12345", false, 0.0));
        m.fromMap(parameters);
        ((ProcessInstance) processInstance).updateVariables(m);

        processInstance.error().get().retrigger();
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.status());

        Model result = (Model) processInstance.variables();
        assertEquals(2, result.toMap().size());
        assertTrue(((Order) result.toMap().get("order")).getTotal() > 0);

        ProcessInstances<? extends Model> orderItemProcesses = orderItemsProcess.instances();

        ProcessInstance<?> childProcessInstance = getFirst(orderItemProcesses);

        List<WorkItem> workItems = childProcessInstance.workItems(policy);
        assertEquals(1, workItems.size());

        childProcessInstance.completeWorkItem(workItems.get(0).getId(), null, policy);

        assertEquals(ProcessInstance.STATE_COMPLETED, childProcessInstance.status());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.status());

        assertEmpty(orderProcess.instances());
        assertEmpty(orderItemsProcess.instances());
    }
}
