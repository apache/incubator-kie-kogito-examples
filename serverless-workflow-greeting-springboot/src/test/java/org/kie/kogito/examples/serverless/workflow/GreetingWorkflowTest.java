package org.kie.kogito.examples.serverless.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoServerlessWorkflowGreetingApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
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
