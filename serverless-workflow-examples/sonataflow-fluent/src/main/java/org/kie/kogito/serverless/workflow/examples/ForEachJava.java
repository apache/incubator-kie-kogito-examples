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
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.kie.kogito.serverless.workflow.executor.StaticWorkflowApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.serverlessworkflow.api.Workflow;

import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.call;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.expr;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.java;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.forEach;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.operation;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.workflow;

public class ForEachJava {

    private static final Logger logger = LoggerFactory.getLogger(ForEachJava.class);

    public static void main(String[] args) {
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            // execute the flow passing the list of names and the file name
            logger.info(application.execute(getWorkflow(), Map.of("names", Arrays.asList("Javi", "Mark", "Kris", "Alessandro"), "fileName", "message.txt")).getWorkflowdata().toPrettyString());
        }
    }

    static Workflow getWorkflow() {
        // this flow illustrate the usage of foreach and how to use java to perform task that are not part of sw spec.
        // The flow accepts a list of names and suffix them with a message read from a file 
        return workflow("ForEachExample")
                // first load the message from the file and store it in message property
                .start(operation().action(call(java("getMessage", ForEachJava::addAdvice), ".fileName")))
                // then for each element in input names concatenate it with that message
                .next(forEach(".names").loopVar("name").outputCollection(".messages")
                        // jq expression that suffix each name with the message retrieved from the file
                        .action(call(expr("concat", "$name+.adviceMessage")))
                        // only return messages list as result of the flow
                        .outputFilter("{messages}"))
                .end().build();
    }

    // Java method invoked from workflow accepts one parameter, which might be a Map or a primitive/wrapper type, depending on the args provided in the flow
    // In this case, we are passing the name of a file  in the classpath, so the argument is a string
    // Java method return type is always a Map<String,Object> (if not output,it should return an empty map). In this case, 
    // we are adding an advice message to the flow model read from the file. If the file cannot be read, we return empty map.   
    private static Map<String, Object> addAdvice(String fileName) {
        try (InputStream is = ClassLoader.getSystemResourceAsStream(fileName)) {
            if (is != null) {
                return Collections.singletonMap("adviceMessage", new String(is.readAllBytes()));
            }
        } catch (IOException io) {
            logger.warn("Error reading file " + fileName + " from classpath", io);
        }
        return Collections.emptyMap();
    }

}
