/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.resources.process;

public class JobServiceLocalProcessTestResource extends LocalQuarkusProcessTestResource {

    public static final String NAME = "jobs-service";
    //"test-resources/jobs-service.jar" is fetched during maven build, check on pom.xml
    public static final String JOBS_SERVICE_PATH = System.getProperty("jobs.service.path", "test-resources/jobs-service.jar");

    public JobServiceLocalProcessTestResource() {
        super(NAME, JOBS_SERVICE_PATH);
    }
}
