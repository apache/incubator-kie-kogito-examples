package org.kie.kogito.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.kogito.examples.test.RecordedOutputWorkItemHandler;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;

public class WorkItemHandlerConfig extends DefaultWorkItemHandlerConfig {
    
    private RecordedOutputWorkItemHandler handler = new RecordedOutputWorkItemHandler();
    private final List<String> supportedHandlers = Arrays.asList("AssignDepartmentAndManager",
                                                                "CalculatePaymentDate",
                                                                "CalculateVacationDays",
                                                                "CalculateTaxRate",
                                                                "ValidateEmployee",
                                                                "AssignIdAndEmail",
                                                                "DecisionTask");
    
    @Override
    public WorkItemHandler forName(String name) {
                
 
        if (supportedHandlers.contains(name)) {            
            return handler;
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
