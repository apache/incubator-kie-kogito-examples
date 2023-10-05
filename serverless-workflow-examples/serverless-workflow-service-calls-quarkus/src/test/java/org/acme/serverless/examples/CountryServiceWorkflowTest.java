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
package org.acme.serverless.examples;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(RestCountriesMockServer.class)
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

        Model result = (Model) processInstance.variables();
        assertEquals(1, result.toMap().size());

        JsonNode workflowDataNode = (JsonNode) result.toMap().get("workflowdata");
        assertNotNull(workflowDataNode);
        assertEquals("Athens", workflowDataNode.get("capital").textValue());
        assertEquals("Europe", workflowDataNode.get("region").textValue());
        assertEquals("Greece", workflowDataNode.get("name").textValue());
        assertEquals("Small/Medium", workflowDataNode.get("classifier").textValue());
    }

}
