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
package org.acme.examples.test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;

public class RecordedOutputWorkItemHandler implements KogitoWorkItemHandler {

    private Map<String, Function<KogitoWorkItem, Map<String, Object>>> recorded = new HashMap<>();

    @Override
    public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        Map<String, Object> results = recorded.remove(workItem.getParameter("TaskName")).apply(workItem);

        manager.completeWorkItem(workItem.getStringId(), results);
    }

    @Override
    public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {

    }

    public void record(String name, Function<KogitoWorkItem, Map<String, Object>> item) {
        this.recorded.put(name, item);
    }
}
