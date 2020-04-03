package org.kie.kogito.examples.onboarding;

import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.kogito.cloud.workitems.DiscoveredServiceWorkItemHandler;
import org.kie.kogito.cloud.workitems.HttpMethods;
import org.kie.kogito.cloud.workitems.ServiceInfo;

public class DecisionTaskWorkItemHandler extends DiscoveredServiceWorkItemHandler {

    public DecisionTaskWorkItemHandler() {
        if ("true".equalsIgnoreCase(System.getProperty("local"))) {
            this.addServices("id", new ServiceInfo("http://localhost:8081/id", null));
            this.addServices("department", new ServiceInfo("http://localhost:8081/department/first", null));
            this.addServices("employeeValidation", new ServiceInfo("http://localhost:8081/employee-validation/first", null));
            this.addServices("vacations/days", new ServiceInfo("http://localhost:8082/vacations/days", null));
            this.addServices("taxes/rate", new ServiceInfo("http://localhost:8082/taxes/rate", null));
            this.addServices("payments/date", new ServiceInfo("http://localhost:8082/payments/date", null));
        }
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        Map<String, Object> results = discoverAndCall(workItem, System.getenv("NAMESPACE"), "Decision", HttpMethods.POST);

        manager.completeWorkItem(workItem.getId(), results);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

    }

    @Override
    public String getName() {
        return "DecisionTask";
    }

}
