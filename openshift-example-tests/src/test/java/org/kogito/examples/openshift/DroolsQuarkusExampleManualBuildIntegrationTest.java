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
import java.util.concurrent.TimeUnit;

import cz.xtf.core.http.Https;
import org.junit.BeforeClass;
import org.kogito.examples.openshift.deployment.Deployer;
import org.kogito.examples.openshift.deployment.HttpDeployment;

public class DroolsQuarkusExampleManualBuildIntegrationTest extends DroolsQuarkusExampleTestBase {

    private static HttpDeployment kogitoDeployment;

    @BeforeClass
    public static void setUpManualBuild() throws MalformedURLException {
        kogitoDeployment = Deployer.deployKaasUsingS2iAndWait(project, new URL(ASSETS_URL), GIT_CONTEXT_DIR, TestConfig.getKaasS2iQuarkusBuilderImage(), TestConfig.getKaasQuarkusRuntimeImage());
        // Wait until OpenShift route is ready to serve requests
        Https.doesUrlReturnCode(kogitoDeployment.getRouteUrl().toExternalForm(), 404).timeout(TimeUnit.MINUTES, 2L).waitFor();
    }

    @Override
    protected HttpDeployment getKogitoDeployment() {
        return kogitoDeployment;
    }
}
