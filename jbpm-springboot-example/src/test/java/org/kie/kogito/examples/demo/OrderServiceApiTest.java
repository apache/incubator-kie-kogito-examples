package org.kie.kogito.examples.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.kogito.Model;
import org.kie.kogito.examples.DemoApplication;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.WorkItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class OrderServiceApiTest {

    @Autowired
    @Qualifier("demo.orders")
    Process<? extends Model> orderProcess;

    @Autowired
    @Qualifier("demo.orderItems")
    Process<? extends Model> orderItemsProcess;

    @Autowired
    @Qualifier("persons")
    Process<? extends Model> personProcess;

    @Test
    public void testOrderProcess() {
        assertNotNull(orderProcess);

        Model m = orderProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("approver",
                       "john");
        parameters.put("order",
                       new Order("12345",
                                 false,
                                 0.0));
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = orderProcess.createInstance(m);
        processInstance.start();

        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE,
                     processInstance.status());
        Model result = (Model) processInstance.variables();
        assertEquals(2,
                     result.toMap().size());
        assertTrue(((Order) result.toMap().get("order")).getTotal() > 0);

        ProcessInstances<? extends Model> orderItemProcesses = orderItemsProcess.instances();
        assertEquals(1,
                     orderItemProcesses.values().size());

        ProcessInstance<?> childProcessInstance = orderItemProcesses.values().iterator().next();

        List<WorkItem> workItems = childProcessInstance.workItems();
        assertEquals(1,
                     workItems.size());

        childProcessInstance.completeWorkItem(workItems.get(0).getId(),
                                              null);

        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED,
                     childProcessInstance.status());
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED,
                     processInstance.status());

        // no active process instances for both orders and order items processes
        assertEquals(0,
                     orderProcess.instances().values().size());
        assertEquals(0,
                     orderItemsProcess.instances().values().size());
    }

    @Test
    public void testPersonsProcessIsAdult() {
        Model m = personProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("person",
                       new Person("John Doe",
                                  20));
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = personProcess.createInstance(m);
        processInstance.start();

        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED,
                     processInstance.status());
        Model result = (Model) processInstance.variables();
        assertEquals(1,
                     result.toMap().size());
        assertTrue(((Person) result.toMap().get("person")).isAdult());
    }

    @Test
    public void testPersonsProcessIsChild() {
        Model m = personProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("person",
                       new Person("Jenny Quark",
                                  14));
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = personProcess.createInstance(m);
        processInstance.start();

        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE,
                     processInstance.status());
        Model result = (Model) processInstance.variables();
        assertEquals(1,
                     result.toMap().size());
        assertFalse(((Person) result.toMap().get("person")).isAdult());

        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1,
                     workItems.size());

        processInstance.completeWorkItem(workItems.get(0).getId(),
                                         null);

        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED,
                     processInstance.status());
    }
}