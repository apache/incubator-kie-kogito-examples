package org.kie.kogito.examples.demo;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.kogito.Model;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.examples.DemoApplication;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.services.identity.StaticIdentityProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class PersonProcessTest {


    @Autowired
    @Qualifier("persons")
    Process<? extends Model> personProcess;

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

        assertEquals(ProcessInstance.STATE_COMPLETED,
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

        assertEquals(ProcessInstance.STATE_ACTIVE,
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

        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.status());
    }
    
    
    @Test
    public void testChildWithSecurityPolicy() {
        Model m = personProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("person", new Person("Jenny Quark", 14));
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = personProcess.createInstance(m);
        processInstance.start();
        
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.status());
        Model result = (Model)processInstance.variables();
        assertEquals(1, result.toMap().size());
        assertFalse(((Person)result.toMap().get("person")).isAdult());
        
        StaticIdentityProvider identity = new StaticIdentityProvider("admin");
        SecurityPolicy policy = SecurityPolicy.of(identity);
        
        List<WorkItem> workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());
        
        processInstance.completeWorkItem(workItems.get(0).getId(), null, policy);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.status()); 
    }
    
    @Test
    public void testChildWithSecurityPolicyNotAuthorized() {
        Model m = personProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("person", new Person("Jenny Quark", 14));
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = personProcess.createInstance(m);
        processInstance.start();
        
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.status());
        Model result = (Model)processInstance.variables();
        assertEquals(1, result.toMap().size());
        assertFalse(((Person)result.toMap().get("person")).isAdult());
        
        StaticIdentityProvider identity = new StaticIdentityProvider("john");
        SecurityPolicy policy = SecurityPolicy.of(identity);
        
        List<WorkItem> workItems = processInstance.workItems(policy);
        assertEquals(0, workItems.size());
        
        processInstance.abort();
        
        assertEquals(ProcessInstance.STATE_ABORTED, processInstance.status()); 
    }
}
