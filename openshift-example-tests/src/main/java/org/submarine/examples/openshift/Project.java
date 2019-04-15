/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.submarine.examples.openshift;

import cz.xtf.core.openshift.OpenShift;
import cz.xtf.core.openshift.OpenShifts;

public class Project {

    private String name;
    private OpenShift master;

    private Project(String name) {
        this.name = name;
        this.master = OpenShifts.master(name);
    }

    public String getName() {
        return name;
    }

    public OpenShift getMaster() {
        return master;
    }

    public void delete() {
        OpenShifts.master().deleteProject(name);
    }

    public static Project create(String name) {
        OpenShifts.master().createProjectRequest(name);
        return new Project(name);
    }
}
