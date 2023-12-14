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
package org.kie.kogito.dmn.quarkus.example.mock;

import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.api.core.event.AfterEvaluateAllEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateAllEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MockDMNRuntimeEventListener implements DMNRuntimeEventListener {

    Map<String, Integer> calls = new HashMap<>();

    public Map<String, Integer> getCalls() {
        return calls;
    }

    public void reset() {
        calls.clear();
    }

    @Override
    public void beforeEvaluateAll(BeforeEvaluateAllEvent event) {
        record("beforeEvaluateAll");
    }

    @Override
    public void afterEvaluateAll(AfterEvaluateAllEvent event) {
        record("afterEvaluateAll");
    }

    private void record(String methodName) {
        if (calls.containsKey(methodName)) {
            calls.put(methodName, calls.get(methodName) + 1);
        } else {
            calls.put(methodName, 1);
        }
    }

}
