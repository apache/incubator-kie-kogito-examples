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
package org.kie.kogito.examples.serverless.workflow;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoServerlessWorkflowGreetingApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// reset spring context after each test method
public class GreetingWorkflowTest {

    @Autowired
    @Qualifier("jsongreet")
    Process<? extends Model> jsonWorkflow;

    @Autowired
    @Qualifier("yamlgreet")
    Process<? extends Model> yamlWorkflow;

    @Test
    public void testGreetingWorkflowJSON() {
        assertNotNull(jsonWorkflow);

        Model m = jsonWorkflow.createModel();

        JsonNode testDataObject = null;

        try {
            String testData = "{\"name\": \"John\", \"language\": \"English\"}";
            ObjectMapper mapper = new ObjectMapper();
            testDataObject = mapper.readTree(testData);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("workflowdata", testDataObject);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = jsonWorkflow.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
    }

    @Test
    public void testGreetingWorkflowYAML() {
        assertNotNull(yamlWorkflow);

        Model m = yamlWorkflow.createModel();

        JsonNode testDataObject = null;

        try {
            String testData = "{\"name\": \"John\", \"language\": \"English\"}";
            ObjectMapper mapper = new ObjectMapper();
            testDataObject = mapper.readTree(testData);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("workflowdata", testDataObject);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = yamlWorkflow.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
    }
}
