package org.kie.kogito.examples.test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;


public class RecordedOutputWorkItemHandler implements WorkItemHandler {
    
    private Map<String, Function<WorkItem, Map<String, Object>>> recorded = new HashMap<>();

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        Map<String, Object> results = recorded.remove(workItem.getParameter("TaskName")).apply(workItem);

        manager.completeWorkItem(workItem.getId(), results);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

    }

    public void record(String name, Function<WorkItem, Map<String, Object>> item) {
        this.recorded.put(name, item);
    }
}
