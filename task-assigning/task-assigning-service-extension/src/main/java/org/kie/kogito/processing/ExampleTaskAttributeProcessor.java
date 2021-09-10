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

package org.kie.kogito.processing;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.taskassigning.model.processing.TaskAttributesProcessor;
import org.kie.kogito.taskassigning.model.processing.TaskInfo;

/**
 * Task Attributes Processor example.
 * This scaffold class shows how user provided task attributes processors can be implemented.
 * For more information see: https://docs.kogito.kie.org/latest/html_single/#proc-create-custom-task-attributes-processors_kogito-configuring
 */
@ApplicationScoped
public class ExampleTaskAttributeProcessor implements TaskAttributesProcessor {

    /**
     * Indicates the priority of this processor when multiple task attributes processors are applied, lower priorities
     * executes first.
     */
    @Override
    public int getPriority() {
        return 50;
    }

    /**
     * Indicates if the processor is enabled. Disabled processors are not applied.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Executed when a new human task is created in the kogito runtimes, or changes in the TaskInfo information for an
     * existing human task are detected. The targetAttributes will be assigned to the internal Task
     * counterpart managed by OptaPlanner.
     *
     * @param taskInfo Information about the kogito runtimes human task that was created or modified.
     * @param targetAttributes Attributes to assign to the Task counterpart managed by OptaPlanner.
     */
    @Override
    public void process(TaskInfo taskInfo, Map<String, Object> targetAttributes) {
        // custom attribute calculated by using the TaskInfo information or any other procedure.
        // Note: that this execution can be produced many times during the task life cycle, so it shouldn't be a blocking
        // or long running operation.

        // Object myCustomAttributeValue = new Object();
        //targetAttributes.put("myCustomAttribute", myCustomAttributeValue);
    }
}
