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

import java.util.concurrent.TimeUnit;

import cz.xtf.core.http.Https;
import cz.xtf.core.waiting.SimpleWaiter;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.kogito.examples.openshift.deployment.HttpDeployment;
import org.kogito.examples.openshift.deployment.OperatorDeployer;
import org.kogito.examples.openshift.operator.KogitoApp;
import org.kogito.examples.openshift.operator.KogitoAppDoneable;
import org.kogito.examples.openshift.operator.KogitoAppList;
import org.kogito.examples.openshift.operator.model.Build;
import org.kogito.examples.openshift.operator.model.Env;
import org.kogito.examples.openshift.operator.model.GitSource;
import org.kogito.examples.openshift.operator.model.Service;
import org.kogito.examples.openshift.operator.model.Spec;

// Test is unstable due to https://issues.jboss.org/browse/KOGITO-127
public class OnboardingQuarkusExampleKogitoOperatorIntegrationTest extends OnboardingQuarkusExampleTestBase {

    private static HttpDeployment onboardingDeployment;

    @BeforeAll
    public static void setUpOperator() {
        NonNamespaceOperation<KogitoApp, KogitoAppList, KogitoAppDoneable, Resource<KogitoApp, KogitoAppDoneable>> kogitoOperatorClient = OperatorDeployer.deployKogitoOperator(project);

        kogitoOperatorClient.create(getHrKogitoApp());
        project.getMaster().waiters().areExactlyNPodsRunning(1, HR_DEPLOYMENT_NAME).timeout(TimeUnit.MINUTES, 30L).waitFor();

        kogitoOperatorClient.create(getPayrollKogitoApp());
        project.getMaster().waiters().areExactlyNPodsRunning(1, PAYROLL_DEPLOYMENT_NAME).timeout(TimeUnit.MINUTES, 30L).waitFor();

        kogitoOperatorClient.create(getOnboardingKogitoApp());
        project.getMaster().waiters().areExactlyNPodsRunning(1, ONBOARDING_DEPLOYMENT_NAME).timeout(TimeUnit.MINUTES, 30L).waitFor();

        onboardingDeployment = new HttpDeployment(project, ONBOARDING_DEPLOYMENT_NAME);
        HttpDeployment payrollDeployment = new HttpDeployment(project, ONBOARDING_DEPLOYMENT_NAME);
        HttpDeployment hrDeployment = new HttpDeployment(project, ONBOARDING_DEPLOYMENT_NAME);
        // Wait until OpenShift routes are ready to serve requests
        new SimpleWaiter(() -> Https.getCode(onboardingDeployment.getRouteUrl().toExternalForm()) != 503).reason("Waiting for onboarding deployment route to connect to pod.").timeout(TimeUnit.SECONDS, 30L).waitFor();
        new SimpleWaiter(() -> Https.getCode(payrollDeployment.getRouteUrl().toExternalForm()) != 503).reason("Waiting for payroll deployment route to connect to pod.").timeout(TimeUnit.SECONDS, 30L).waitFor();
        new SimpleWaiter(() -> Https.getCode(hrDeployment.getRouteUrl().toExternalForm()) != 503).reason("Waiting for hr deployment route to connect to pod.").timeout(TimeUnit.SECONDS, 30L).waitFor();
    }

    private static KogitoApp getOnboardingKogitoApp() {
        GitSource gitSource = new GitSource();
        gitSource.setUri(ASSETS_URL);
        gitSource.setContextDir(ONBOARDING_GIT_CONTEXT_DIR);

        Build build = new Build();
        build.setGitSource(gitSource);
        TestConfig.getMavenMirrorUrl().ifPresent(mavenMirrorUrl -> build.addEnv(new Env("MAVEN_MIRROR_URL", mavenMirrorUrl)));

        Service service = new Service();
        service.addLabel("onboarding", "process");

        Spec spec = new Spec();
        spec.setBuild(build);
        spec.addEnv(new Env("NAMESPACE", project.getName()));
        spec.setService(service);

        KogitoApp kogitoApp = new KogitoApp();
        kogitoApp.getMetadata().setName(ONBOARDING_DEPLOYMENT_NAME);
        kogitoApp.setSpec(spec);

        return kogitoApp;
    }

    private static KogitoApp getPayrollKogitoApp() {
        GitSource gitSource = new GitSource();
        gitSource.setUri(ASSETS_URL);
        gitSource.setContextDir(PAYROLL_GIT_CONTEXT_DIR);

        Build build = new Build();
        build.setGitSource(gitSource);
        TestConfig.getMavenMirrorUrl().ifPresent(mavenMirrorUrl -> build.addEnv(new Env("MAVEN_MIRROR_URL", mavenMirrorUrl)));

        Service service = new Service();
        service.addLabel("taxRate", "process");
        service.addLabel("vacationDays", "process");
        service.addLabel("paymentDate", "process");

        Spec spec = new Spec();
        spec.setBuild(build);
        spec.setService(service);

        KogitoApp kogitoApp = new KogitoApp();
        kogitoApp.getMetadata().setName(PAYROLL_DEPLOYMENT_NAME);
        kogitoApp.setSpec(spec);

        return kogitoApp;
    }

    private static KogitoApp getHrKogitoApp() {
        GitSource gitSource = new GitSource();
        gitSource.setUri(ASSETS_URL);
        gitSource.setContextDir(HR_GIT_CONTEXT_DIR);

        Build build = new Build();
        build.setGitSource(gitSource);
        TestConfig.getMavenMirrorUrl().ifPresent(mavenMirrorUrl -> build.addEnv(new Env("MAVEN_MIRROR_URL", mavenMirrorUrl)));

        Service service = new Service();
        service.addLabel("department", "process");
        service.addLabel("id", "process");
        service.addLabel("employeeValidation", "process");

        Spec spec = new Spec();
        spec.setBuild(build);
        spec.setService(service);

        KogitoApp kogitoApp = new KogitoApp();
        kogitoApp.getMetadata().setName(HR_DEPLOYMENT_NAME);
        kogitoApp.setSpec(spec);

        return kogitoApp;
    }

    @Override
    protected HttpDeployment getKogitoDeployment() {
        return onboardingDeployment;
    }
}
