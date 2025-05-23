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
package org.kie.kogito.examples.sw.custom;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.Map;
import org.kie.kogito.examples.sw.custom.CalculatorClient.OperationId;
import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;

@ApplicationScoped
public class RPCCustomWorkItemHandler extends WorkflowWorkItemHandler {

    public static final String NAME = "RPCCustomWorkItemHandler";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String OPERATION = "operation";

    @Override
    protected Object internalExecute(KogitoWorkItem workItem, Map<String, Object> parameters) {
        try {
            Iterator<?> iter = parameters.values().iterator();
            Map<String, Object> metadata = workItem.getNodeInstance().getNode().getMetaData();
            String operationId = (String) metadata.get(OPERATION);
            if (operationId == null) {
                throw new IllegalArgumentException("Operation is a mandatory parameter");
            }
            return CalculatorClient.invokeOperation((String) metadata.getOrDefault(HOST, "localhost"), (int) metadata.getOrDefault(PORT, 8082),
                    OperationId.valueOf(operationId.toUpperCase()), (Integer) iter.next(), (Integer) iter.next());
        } catch (IOException io) {
            throw new UncheckedIOException(io);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
