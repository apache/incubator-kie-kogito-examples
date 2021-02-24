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
package org.kie.kogito.hr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.process.instance.impl.humantask.HumanTaskTransition;
import org.jbpm.process.instance.impl.humantask.phases.Claim;
import org.jbpm.process.instance.impl.workitem.Complete;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class HiringProcessTest {

    
    @Named("hiring")
    @Inject
    Process<? extends Model> hiringProcess;
    
    @Test
    public void testApprovalProcess() {
                
        assertNotNull(hiringProcess);

        Model m = hiringProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("candidate", new Candidate("Jon Snow", "jsnow@example.com", 30000, "Java, Kogito"));
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = hiringProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status()); 

        SecurityPolicy policy = SecurityPolicy.of(IdentityProviders.of("john", Arrays.asList("HR", "IT")));

        processInstance.workItems(policy);

        List<WorkItem> workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());
        Map<String, Object> results = new HashMap<>();
        results.put("approve", true);
        processInstance.completeWorkItem(workItems.get(0).getId(), results, policy);

        processInstance.workItems(policy);
        
        workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());
        
        results.put("approve", false);
        processInstance.completeWorkItem(workItems.get(0).getId(), results, policy);
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
        
        Model result = (Model)processInstance.variables();
        assertEquals(3, result.toMap().size());
        assertEquals(true , result.toMap().get("hr_approval"));
        assertEquals(false, result.toMap().get("it_approval"));
    }

}
