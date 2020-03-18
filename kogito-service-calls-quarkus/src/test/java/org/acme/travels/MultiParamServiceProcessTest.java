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
public class MultiParamServiceProcessTest {

    
    @Named("multiparams")
    @Inject
    Process<? extends Model> multiparamsProcess;
    
    @Test
    public void testServiceWithMultipleParams() {
                
        assertNotNull(multiparamsProcess);
        
        Model m = multiparamsProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "john");
        parameters.put("age", 44);
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = multiparamsProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status()); 
        Model result = (Model)processInstance.variables();
        assertEquals(2, result.toMap().size());
        
    }
}
