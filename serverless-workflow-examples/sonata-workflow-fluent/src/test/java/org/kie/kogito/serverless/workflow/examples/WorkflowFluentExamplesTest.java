/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.serverless.workflow.examples;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.serverless.workflow.executor.StaticWorkflowApplication;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;
import org.kie.kogito.serverless.workflow.utils.WorkflowFormat;

import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.serverlessworkflow.api.Workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.jsonObject;

public class WorkflowFluentExamplesTest {

    private static StaticWorkflowApplication application;

    @BeforeAll
    static void init() {
        application = StaticWorkflowApplication.create();
    }

    @AfterAll
    static void cleanUp() {
        application.close();
    }

    @Test
    void testHelloWorldDefinition() {
        ObjectNode expected = jsonObject().put("greeting", "Hello World").put("mantra", "Serverless Workflow is awesome!");
        assertThat(application.execute(HelloWorld.getWorkflow(), Collections.emptyMap()).getWorkflowdata()).isEqualTo(expected);
    }

    @Test
    void testForEachJavaDefinition() {
        assertThat(application.execute(ForEachJavaExample.getWorkflow(), Map.of("names", Arrays.asList("Javi", "Mark"), "fileName", "message.txt")).getWorkflowdata().get("messages"))
                .containsExactly(new TextNode("Javi , congratulations, you are a happy user of serverless workflow"),
                        new TextNode("Mark , congratulations, you are a happy user of serverless workflow"));
    }

    @Test
    void testJQInterpolation() {
        assertThat(application.execute(JQInterpolation.getWorkflow(), Map.of("name", "Javierito", "language", "Spanish")).getWorkflowdata().get("greeting"))
                .isEqualTo(new TextNode("My name is Javierito. My language is Spanish"));
    }

    @Test
    void testConcatenationDefinition() {
        assertThat(application.execute(ConcatenationExample.getWorkflow(), Map.of("name", "Javier", "surname", "Tirado")).getWorkflowdata())
                .isEqualTo(new TextNode("My name is Javier and my surname is Tirado"));
    }

    @Test
    void testDivissionDefinition() throws IOException {
        assertThat(application.execute(
                getWorkflow("division.sw.json"), Map.of("number1", 4, "number2", 2)).getWorkflowdata().get("response")).isEqualTo(new IntNode(2));
    }

    @Test
    void testExpressionDefinition() throws IOException {
        assertThat(application.execute(
                getWorkflow("expression.sw.json"), Map.of("numbers", Arrays.asList(Map.of("x", 3, "y", 4), Map.of("x", 5, "y", 7)))).getWorkflowdata().get("result")).isEqualTo(new IntNode(5));
    }

    private Workflow getWorkflow(String filename) throws IOException {
        try (Reader in = new InputStreamReader(ClassLoader.getSystemResourceAsStream(filename))) {
            return ServerlessWorkflowUtils.getWorkflow(in, WorkflowFormat.JSON);
        }
    }
}
