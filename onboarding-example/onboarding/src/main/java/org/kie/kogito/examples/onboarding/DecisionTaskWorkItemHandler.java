package org.kie.kogito.examples.onboarding;

import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.submarine.cloud.workitems.DiscoveredServiceWorkItemHandler;
import org.kie.submarine.cloud.workitems.HttpMethods;
import org.kie.submarine.cloud.workitems.ServiceInfo;

public class DecisionTaskWorkItemHandler extends DiscoveredServiceWorkItemHandler {

    private String namespace = System.getenv("NAMESPACE");

    public DecisionTaskWorkItemHandler() {
        if ("true".equalsIgnoreCase(System.getProperty("local"))) {
            this.serviceEndpoints.put("id", new ServiceInfo("http://localhost:8081/id", null));
            this.serviceEndpoints.put("department", new ServiceInfo("http://localhost:8081/department", null));
            this.serviceEndpoints.put("employeeValidation", new ServiceInfo("http://localhost:8081/employeeValidation", null));
            this.serviceEndpoints.put("vacationDays", new ServiceInfo("http://localhost:8082/vacationDays", null));
            this.serviceEndpoints.put("taxRate", new ServiceInfo("http://localhost:8082/taxRate", null));
            this.serviceEndpoints.put("paymentDate", new ServiceInfo("http://localhost:8082/paymentDate", null));
        }
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

        Map<String, Object> results = discoverAndCall(workItem, namespace, "Decision", HttpMethods.POST);

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
