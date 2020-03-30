package org.kie.kogito.examples.hr;

import java.util.HashSet;
import java.util.Set;

public class EmployeeRepository {

    private static EmployeeRepository INSTANCE = new EmployeeRepository();
    
    private Set<String> registered = new HashSet<>();
    
    public static EmployeeRepository get() {
        return INSTANCE;
    }
    
    /**
     * @return true if the employee is already registered 
     */
    public boolean isRegistered(String personalId) {
        return registered.contains(personalId);
    }

    /**
     * @return true if the employee is already registered 
     */
    public void register(Employee emp) {
        if (!isRegistered(emp.getPersonalId())) {
            registered.add(emp.getPersonalId());
        }
    }

}
