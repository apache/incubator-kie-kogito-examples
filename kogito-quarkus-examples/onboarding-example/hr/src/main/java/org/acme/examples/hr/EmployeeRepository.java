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
     * @return false if the employee is already registered
     */
    public boolean isNotRegistered(String personalId) {
        return !isRegistered(personalId);
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
