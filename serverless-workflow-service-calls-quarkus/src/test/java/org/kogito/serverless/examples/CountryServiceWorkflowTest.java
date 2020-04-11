package org.kogito.serverless.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

@QuarkusTest
public class CountryServiceWorkflowTest {

    @Named("jsonservicecall")
    @Inject
    Process<? extends Model> jsonServiceCallWorkflow;

    @Test
    public void testJsonServiceCallWorkflow() throws Exception {
        assertNotNull(jsonServiceCallWorkflow);

        Model m = jsonServiceCallWorkflow.createModel();
        Map<String, Object> parameters = new HashMap<>();

        String testCountryStr = "{\"name\": \"Greece\"}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode workflowDataInput = mapper.readTree(testCountryStr);

        parameters.put("workflowdata", workflowDataInput);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = jsonServiceCallWorkflow.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());

        Model result = (Model)processInstance.variables();
        assertEquals(1, result.toMap().size());

        JsonNode workflowDataNode = (JsonNode) result.toMap().get("workflowdata");
        assertNotNull(workflowDataNode);
        assertEquals("Athens", workflowDataNode.get("capital").textValue());
        assertEquals("Europe", workflowDataNode.get("region").textValue());
        assertEquals("Greece", workflowDataNode.get("name").textValue());
        assertEquals("Small/Medium", workflowDataNode.get("classifier").textValue());
    }

}
