package org.acme.travels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TimersProcessTest {

    
    @Named("timers")
    @Inject
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
