package org.kie.kogito.examples.hr;

import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.RuleUnitData;
import org.kie.kogito.rules.SingletonStore;

public class Department implements RuleUnitData {
    final SingletonStore<Employee> employee = DataSource.createSingleton();
    public SingletonStore<Employee> getEmployee() {
        return employee;
    }
}