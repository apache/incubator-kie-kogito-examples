package org.acme.travels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.tests.KogitoInfinispanSpringbootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = KogitoInfinispanSpringbootApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class TimersProcessTest {

    
    @Autowired
    @Qualifier("timers")
    Process<? extends Model> timersProcess;
    
    @Test
    public void testTimersProcess() throws InterruptedException {
                
        assertNotNull(timersProcess);
        
        Model m = timersProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("delay", "PT3S");
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = timersProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status()); 
        
        Thread.sleep(5000);
        
        Optional<? extends ProcessInstance<?>> exists = timersProcess.instances().findById(processInstance.id());
        assertFalse(exists.isPresent());
       
    }
}
