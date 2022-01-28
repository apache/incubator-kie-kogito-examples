/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.acme;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Helper class containing methods executed from the serverless workflow scripts.
 */
public class QueryAnswerServiceScripts {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryAnswerServiceScripts.class);

    private QueryAnswerServiceScripts() {
    }

    public static void initializationScript(KogitoProcessContext kcontext) {
        ObjectNode workflowData = (com.fasterxml.jackson.databind.node.ObjectNode) kcontext.getVariable("workflowdata");
        workflowData.put("processInstanceId", kcontext.getProcessInstance().getStringId());
    }

    public static void printWorkflowData(String messsage, KogitoProcessContext kcontext) {
        ObjectNode workflowData = (com.fasterxml.jackson.databind.node.ObjectNode) kcontext.getVariable("workflowdata");
        LOGGER.debug("{}, workflowdata: {}", messsage, workflowData);
    }
}
