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
package org.kie.flexible.kogito.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.kie.flexible.kogito.example.model.Product;
import org.kie.flexible.kogito.example.model.State;
import org.kie.flexible.kogito.example.model.SupportCase;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.impl.Sig;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ServiceDeskProcessTest {

    
    @Named("serviceDesk")
    @Inject
    Process<? extends Model> serviceDeskProcess;
    
    @Test
    public void testSupportCaseExample() {
        assertNotNull(serviceDeskProcess);

        ProcessInstance<?> processInstance = createSupportCase();
        addSupportComment(processInstance);
        String questionnaireId = resolveCase(processInstance);
        sendQuestionnaire(processInstance, questionnaireId);
    }

    private ProcessInstance<?> createSupportCase() {
        Model m = serviceDeskProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("supportCase", new SupportCase()
            .setProduct(new Product().setFamily("Middleware").setName("Kogito")));
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = serviceDeskProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status()); 
        Map<String, Object> result = ((Model)processInstance.variables()).toMap();

        assertEquals(2, result.size());
        assertEquals(result.get("supportGroup"), "Kogito");
        SupportCase ticket = (SupportCase) result.get("supportCase");
        assertEquals(State.WAITING_FOR_OWNER, ticket.getState());
        return processInstance;
    }

    private void addSupportComment(ProcessInstance<?> processInstance) {
        processInstance.send(Sig.of("Receive support comment", null));
        Optional<WorkItem> item = processInstance.workItems()
            .stream()
            .filter(wi -> wi.getName().equals("ReceiveSupportComment"))
            .findFirst();
        assertTrue(item.isPresent());
        String id = item.get().getId();
        assertNotNull(id);
        Map<String, Object> params = new HashMap<>();
        params.put("ActorId", "kelly");
        params.put("comment", "What's up");
        processInstance.completeWorkItem(id, params);

        SupportCase ticket = (SupportCase) ((Model)processInstance
            .variables())
            .toMap()
            .get("supportCase");
        assertEquals(State.WAITING_FOR_CUSTOMER, ticket.getState());
        assertEquals(1, ticket.getComments().size());
        assertEquals(params.get("ActorId"), ticket.getComments().get(0).getAuthor());
        assertEquals(params.get("comment"), ticket.getComments().get(0).getText());
        assertNotNull(ticket.getComments().get(0).getDate());
    }

    private String resolveCase(ProcessInstance<?> processInstance) {
        processInstance.send(Sig.of("Resolve Case", null));
        SupportCase ticket = (SupportCase) ((Model)processInstance
            .variables())
            .toMap()
            .get("supportCase");
        assertEquals(State.RESOLVED, ticket.getState());
        Optional<WorkItem> item = processInstance.workItems()
            .stream()
            .filter(wi -> wi.getName().equals("Questionnaire"))
            .findFirst();
        assertTrue(item.isPresent());
        return item.get().getId();
    }

    private void sendQuestionnaire(ProcessInstance<?> processInstance, String questionnaireId) {
        Map<String, Object> params = new HashMap<>();
        params.put("evaluation", 10);
        params.put("comment", "It's great!");
        processInstance.completeWorkItem(questionnaireId, params);
        SupportCase ticket = (SupportCase) ((Model)processInstance
            .variables())
            .toMap()
            .get("supportCase");

        assertEquals(params.get("evaluation"), ticket.getQuestionnaire().getEvaluation());
        assertEquals(params.get("comment"), ticket.getQuestionnaire().getComment());
        assertNotNull(ticket.getQuestionnaire().getDate());
        assertEquals(State.CLOSED, ticket.getState());
    }
}