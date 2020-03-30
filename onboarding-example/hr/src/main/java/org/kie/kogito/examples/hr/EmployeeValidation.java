package org.kie.kogito.examples.hr;

import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.RuleUnitData;
import org.kie.kogito.rules.SingletonStore;

public class EmployeeValidation implements RuleUnitData {
    private final SingletonStore<Employee> employee = DataSource.createSingleton();
    private final SingletonStore<EmployeeValidationModel> validation = DataSource.createSingleton();
    public SingletonStore<Employee> getEmployee() {
        return employee;
    }
    public SingletonStore<EmployeeValidationModel> getValidation() {
        return validation;
    }
}