package org.kie.kogito.examples.hr;

import java.util.HashMap;
import java.util.Map;

public class EmployeeRepository {

    private static EmployeeRepository INSTANCE = new EmployeeRepository();
    
    private Map<String, String> registered = new HashMap<>();
    
    public static EmployeeRepository get() {
        return INSTANCE;
    }
    
    public boolean register(Employee emp) {
        String status = registered.putIfAbsent(emp.getPersonalId(), "exists");
        if (status == null) {
            return false;
        }        
        
        return true;
    }

}
