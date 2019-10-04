/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.examples.hr.department;

import java.io.Serializable;

import org.kie.kogito.examples.hr.Employee;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnitMemory;

public class DepartmentModel implements RuleUnitMemory, Serializable {

    private DataStore<Employee> employees;

    public DepartmentModel( ) {
        this( DataSource.createStore() );
    }

    public DepartmentModel( DataStore<Employee> employees ) {
        this.employees = employees;
    }

    public DataStore<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees( DataStore<Employee> employees ) {
        this.employees = employees;
    }
}