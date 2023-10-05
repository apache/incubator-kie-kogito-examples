/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.examples.hr;

public class DepartmentModel {

    private Employee employee;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    private java.lang.String manager;

    public java.lang.String getManager() {
        return manager;
    }

    public void setManager(java.lang.String manager) {
        this.manager = manager;
    }

    private java.lang.String department;

    public java.lang.String getDepartment() {
        return department;
    }

    public void setDepartment(java.lang.String department) {
        this.department = department;
    }
}
