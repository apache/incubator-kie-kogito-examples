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
import java.util.concurrent.TimeUnit;

import cz.xtf.core.http.Https;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.junit.BeforeClass;
import org.kogito.examples.openshift.deployment.HttpDeployment;
import org.kogito.examples.openshift.deployment.OperatorDeployer;
import org.kogito.examples.openshift.operator.KogitoApp;
import org.kogito.examples.openshift.operator.KogitoAppDoneable;
import org.kogito.examples.openshift.operator.KogitoAppList;
import org.kogito.examples.openshift.operator.model.Build;
import org.kogito.examples.openshift.operator.model.GitSource;
import org.kogito.examples.openshift.operator.model.Spec;

public class DroolsQuarkusExampleKogitoOperatorIntegrationTest extends DroolsQuarkusExampleTestBase {

    private static final String DEPLOYMENT_NAME = "kogito";
    private static HttpDeployment kogitoDeployment;

    @BeforeClass
    public static void setUpOperator() throws MalformedURLException {
        NonNamespaceOperation<KogitoApp, KogitoAppList, KogitoAppDoneable, Resource<KogitoApp, KogitoAppDoneable>> kogitoOperatorClient = OperatorDeployer.deployKogitoOperator(project);

        GitSource gitSource = new GitSource();
        gitSource.setUri(ASSETS_URL);
        gitSource.setContextDir(GIT_CONTEXT_DIR);

        Build build = new Build();
        build.setGitSource(gitSource);

        Spec spec = new Spec();
        spec.setBuild(build);

        KogitoApp kogitoApp = new KogitoApp();
        kogitoApp.getMetadata().setName(DEPLOYMENT_NAME);
        kogitoApp.setSpec(spec);
        kogitoOperatorClient.create(kogitoApp);

        kogitoDeployment = new HttpDeployment(project, DEPLOYMENT_NAME);

        project.getMaster().waiters().areExactlyNPodsRunning(1, DEPLOYMENT_NAME).timeout(TimeUnit.MINUTES, 30L).waitFor();
        // Wait until OpenShift route is ready to serve requests
        Https.doesUrlReturnCode(kogitoDeployment.getRouteUrl().toExternalForm(), 404).timeout(TimeUnit.MINUTES, 2L).waitFor();
    }

    @Override
    protected HttpDeployment getKogitoDeployment() {
        return kogitoDeployment;
    }
}
