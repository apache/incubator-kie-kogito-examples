package org.kie.kogito.examples.hr;

import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnitData;

public class EmployeeValidation implements RuleUnitData {
    private final DataStore<Employee> employee = DataSource.createStore();
    private final DataStore<EmployeeValidationModel> validation = DataSource.createStore();
    public DataStore<Employee> getEmployee() {
        return employee;
    }
    public DataStore<EmployeeValidationModel> getValidation() {
        return validation;
    }
}