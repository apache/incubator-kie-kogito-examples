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

import java.util.Map;

import org.kie.kogito.process.Process;
import org.kie.kogito.serverless.workflow.actions.WorkflowLogLevel;
import org.kie.kogito.serverless.workflow.executor.StaticWorkflowApplication;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.serverlessworkflow.api.Workflow;

import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.call;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.log;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.expr;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.operation;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.workflow;

public class ConcatenationExample {

    private static final Logger logger = LoggerFactory.getLogger(ConcatenationExample.class);

    public static void main(String[] args) {
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            // This flow illustrate the usage of two consecutive function calls
            // create a reusable process for several executions
            Process<JsonNodeModel> process = application.process(getWorkflow());
            // execute it with one person name
            logger.info(application.execute(process, Map.of("name", "Javier", "surname", "Tirado")).getWorkflowdata().toPrettyString());
            // execute it with other person name
            logger.info(application.execute(process, Map.of("name", "Mark", "surname", "Proctor")).getWorkflowdata().toPrettyString());
        }
    }

    static Workflow getWorkflow() {
        return workflow("ExpressionExample")
                // concatenate name 
                .start(operation()
                        .action(call(expr("name", "\"My name is \"+.name")))
                        // you can add several sequential actions into an operation
                        .action(log(WorkflowLogLevel.DEBUG, "\"Response is\"+.response")))
                // concatenate surname
                .next(operation()
                        .action(call(expr("surname", ".response+\" and my surname is \"+.surname")))
                        .outputFilter(".response"))
                .end().build();
    }
}
