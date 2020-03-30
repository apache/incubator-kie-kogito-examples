package org.kie.kogito.examples.hr;

import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.SingletonStore;

public class Department {
    final SingletonStore<Employee> employee = DataSource.createSingleton();
    public SingletonStore<Employee> getEmployee() {
        return employee;
    }
}