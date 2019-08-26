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
import java.util.HashMap;

import org.junit.jupiter.api.BeforeAll;
import org.kogito.examples.openshift.deployment.CliDeployer;
import org.kogito.examples.openshift.deployment.HttpDeployment;
import org.kogito.examples.openshift.deployment.OperatorDeployer;

public class DroolsQuarkusExampleKogitoCliIntegrationTest extends DroolsQuarkusExampleTestBase {

    private static final String DEPLOYMENT_NAME = "kogito";
    private static HttpDeployment kogitoDeployment;

    @BeforeAll
    public static void setUpOperatorAndCliDeployment() throws MalformedURLException {
        OperatorDeployer.deployKogitoOperator(project);
        kogitoDeployment = CliDeployer.deployKaasUsingS2iAndWait(project, DEPLOYMENT_NAME, new URL(ASSETS_URL), GIT_CONTEXT_DIR, new HashMap<>(), new HashMap<>());
    }

    @Override
    protected HttpDeployment getKogitoDeployment() {
        return kogitoDeployment;
    }
}
