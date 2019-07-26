package org.acme.deals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class DealsTest {

	@Inject
	@Named("deals")
	Process<? extends Model> dealsProcess;
	
	@Test
	public void testDealsProcess() {
		
		assertNotNull(dealsProcess);
		
		Model m = dealsProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("deal", "");
        

        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = dealsProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());                 
        
        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        assertEquals("review", workItems.get(0).getName());
        
        processInstance.completeWorkItem(workItems.get(0).getId(), Collections.singletonMap("review", "just a sample review"));
        
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
	}
	
}
