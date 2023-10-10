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
package org.acme.script;

import java.util.Map.Entry;
import java.util.Set;

import org.kie.api.runtime.process.ProcessWorkItemHandlerException;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandlingScript {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandlingScript.class);

    public static void init(KogitoProcessContext kcontext) {
        LOG.debug("start");

        // process instance variables
        Set<Entry<String, Object>> entrySet = kcontext.getProcessInstance().getVariables().entrySet();
        for (Entry<String, Object> entry : entrySet) {
            LOG.debug("{} = {}\n", entry.getKey(), entry.getValue().toString());
        }
        ProcessWorkItemHandlerException exception = (ProcessWorkItemHandlerException) kcontext.getVariable("Error");
        // get strategy
        if (exception != null) {
            String strategy = exception.getStrategy().name();
            kcontext.setVariable("strategy", strategy);
        }
        LOG.debug("end");
    }

    public static void apply(KogitoProcessContext kcontext) {
        LOG.debug("start");
        ProcessWorkItemHandlerException exception = (ProcessWorkItemHandlerException) kcontext.getVariable("Error");
        String strategy = (String) kcontext.getVariable("strategy");
        LOG.debug("strategy: {}", strategy);

        // apply strategy
        if (exception != null && strategy != null && strategy.matches("(RETRY|COMPLETE|ABORT|RETHROW)")) {
            ProcessWorkItemHandlerException exception2 = new ProcessWorkItemHandlerException(exception.getProcessId(),
                    ProcessWorkItemHandlerException.HandlingStrategy.valueOf(strategy),
                    exception.getCause());

            kcontext.setVariable("Error", exception2);
        }
        LOG.debug("end");
    }
}
