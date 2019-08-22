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

package org.kogito.examples.openshift;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cz.xtf.core.openshift.OpenShiftBinary;
import cz.xtf.core.openshift.OpenShifts;
import org.junit.jupiter.api.BeforeAll;
import org.kogito.examples.openshift.deployment.Deployer;
import org.kogito.examples.openshift.deployment.HttpDeployment;

public class OnboardingQuarkusExampleManualBuildIntegrationTest extends OnboardingQuarkusExampleTestBase {

    private static HttpDeployment kogitoDeployment;

    @BeforeAll
    public static void setUpManualBuild() throws MalformedURLException {
        Map<String, String> payrollServiceLabels = new HashMap<>();
        payrollServiceLabels.put("taxRate", "process");
        payrollServiceLabels.put("vacationDays", "process");
        payrollServiceLabels.put("paymentDate", "process");

        Map<String, String> hrServiceLabels = new HashMap<>();
        hrServiceLabels.put("department", "process");
        hrServiceLabels.put("id", "process");
        hrServiceLabels.put("employeeValidation", "process");

        // Add view rights for default role so onboarding service can discover other services.
        OpenShiftBinary masterBinary = OpenShifts.masterBinary(project.getName());
        masterBinary.execute("policy", "add-role-to-user", "view", "-z", "default");

        kogitoDeployment = Deployer.deployKaasUsingS2iAndWait(project, ONBOARDING_DEPLOYMENT_NAME, new URL(ASSETS_URL), ONBOARDING_GIT_CONTEXT_DIR, TestConfig.getKaasS2iQuarkusBuilderImage(), TestConfig.getKaasQuarkusRuntimeImage(), Collections.singletonMap("NAMESPACE", project.getName()), Collections.singletonMap("onboarding", "process"));
        Deployer.deployKaasUsingS2iAndWait(project, PAYROLL_DEPLOYMENT_NAME, new URL(ASSETS_URL), PAYROLL_GIT_CONTEXT_DIR, TestConfig.getKaasS2iQuarkusBuilderImage(), TestConfig.getKaasQuarkusRuntimeImage(), new HashMap<>(), payrollServiceLabels);
        Deployer.deployKaasUsingS2iAndWait(project, HR_DEPLOYMENT_NAME, new URL(ASSETS_URL), HR_GIT_CONTEXT_DIR, TestConfig.getKaasS2iQuarkusBuilderImage(), TestConfig.getKaasQuarkusRuntimeImage(), new HashMap<>(), hrServiceLabels);
    }

    @Override
    protected HttpDeployment getKogitoDeployment() {
        return kogitoDeployment;
    }
}
