package org.acme.travels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ScriptsProcessTest {

    
    @Named("scripts")
    @Inject
    Process<? extends Model> scriptsProcess;
    
    @Test
    public void testNewTraveller() {
                
        assertNotNull(scriptsProcess);
        
        Model m = scriptsProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "john");       
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = scriptsProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status()); 
        Model result = (Model)processInstance.variables();
        assertEquals(2, result.toMap().size());
        assertEquals(result.toMap().get("message"), "Hello john");
    }
}
