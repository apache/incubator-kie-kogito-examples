/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.examples.polyglot;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

@QuarkusTest
public class ApplicantProcessTest {

    @Inject
    Application kogitoApp;

    @Test
    public void testInvalidApplicant() {
        Applicant applicant = new Applicant();
        applicant.setFname("test");
        applicant.setLname("user");
        applicant.setAge(13);
        applicant.setValid(true);

        getApplicantProcessInstance(applicant).start();

        assertFalse(applicant.isValid());
    }

    @Test
    public void testValidApplicant() {
        Applicant applicant = new Applicant();
        applicant.setFname("test");
        applicant.setLname("user");
        applicant.setAge(22);
        applicant.setValid(false);

        getApplicantProcessInstance(applicant).start();

        assertTrue(applicant.isValid());
    }

    private ProcessInstance<?> getApplicantProcessInstance(Applicant applicant) {
        Process<? extends Model> p = kogitoApp.processes().processById("applicantprocess");

        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("applicant", applicant));

        ProcessInstance<?> processInstance = p.createInstance(m);

        return processInstance;
    }
}