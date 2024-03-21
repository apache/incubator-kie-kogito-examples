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

import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.executor.StaticWorkflowApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.serverlessworkflow.api.Workflow;

import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.call;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.expr;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.operation;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.workflow;

public class JQInterpolation {

    private static final Logger logger = LoggerFactory.getLogger(JQInterpolation.class);

    public static void main(String[] args) throws IOException {
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            logger.info(application.execute(getWorkflow(), ObjectMapperFactory.get().createObjectNode().put("name", "Javierito").put("language", "Spanish")).getWorkflowdata().toPrettyString());

        }
    }

    static Workflow getWorkflow() {
        final String INTERPOLATION = "interpolation";
        return workflow("PlayingWithExpression").function(expr(INTERPOLATION, "{greeting: \"My name is \\(.name). My language is \\(.language)\"}"))
                .start(operation().action(call(INTERPOLATION))).end().build();
    }
}
