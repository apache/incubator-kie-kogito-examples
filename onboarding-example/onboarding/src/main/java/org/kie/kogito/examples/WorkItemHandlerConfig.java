package org.kie.kogito.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.kogito.examples.onboarding.DecisionTaskWorkItemHandler;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;

@ApplicationScoped
public class WorkItemHandlerConfig extends DefaultWorkItemHandlerConfig {

    private final Map<String, WorkItemHandler> workItemHandlers = new HashMap<>();
    private final List<String> supportedHandlers = Arrays.asList("AssignDepartmentAndManager",
                                                                "CalculatePaymentDate",
                                                                "CalculateVacationDays",
                                                                "CalculateTaxRate",
                                                                "ValidateEmployee",
                                                                "AssignIdAndEmail",
                                                                "DecisionTask");
    
    @Override
    public WorkItemHandler forName(String name) {
                
        workItemHandlers.putIfAbsent("DecisionTask", new DecisionTaskWorkItemHandler());
        if (supportedHandlers.contains(name)) {
            // use decision task handler (single instance) for all supported handlers that are based on decision calls
            return workItemHandlers.get("DecisionTask");
        }
        
        return super.forName(name);
    }

    @Override
    public Collection<String> names() {
        List<String> names = new ArrayList<>(supportedHandlers);
        names.addAll(super.names());
        return names;
    }
}
