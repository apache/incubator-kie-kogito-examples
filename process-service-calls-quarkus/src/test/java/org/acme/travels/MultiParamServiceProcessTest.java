/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
