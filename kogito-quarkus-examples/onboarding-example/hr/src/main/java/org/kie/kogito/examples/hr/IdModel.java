/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.examples.hr;

public class IdModel {

    private org.kie.kogito.examples.hr.Employee employee;

    public org.kie.kogito.examples.hr.Employee getEmployee() {
        return employee;
    }

    public void setEmployee(org.kie.kogito.examples.hr.Employee employee) {
        this.employee = employee;
    }

    private java.lang.String employeeId;

    public java.lang.String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(java.lang.String employeeId) {
        this.employeeId = employeeId;
    }

    private java.lang.String email;

    public java.lang.String getEmail() {
        return email;
    }

    public void setEmail(java.lang.String email) {
        this.email = email;
    }
}
