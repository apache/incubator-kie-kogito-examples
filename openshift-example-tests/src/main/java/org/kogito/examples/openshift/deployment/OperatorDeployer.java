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

package org.kogito.examples.openshift.deployment;

import java.net.MalformedURLException;
import java.net.URL;

import cz.xtf.core.openshift.OpenShiftBinary;
import cz.xtf.core.openshift.OpenShifts;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.kogito.examples.openshift.Project;
import org.kogito.examples.openshift.operator.KogitoApp;
import org.kogito.examples.openshift.operator.KogitoAppDoneable;
import org.kogito.examples.openshift.operator.KogitoAppList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperatorDeployer {

    private static final Logger logger = LoggerFactory.getLogger(OperatorDeployer.class);

    private static final String OPERATOR_RESOURCE_BASE_URL = "https://raw.githubusercontent.com/kiegroup/kogito-cloud-operator/master/deploy";
    private static final String OPERATOR_SERVICE_ACCOUNT = OPERATOR_RESOURCE_BASE_URL + "/service_account.yaml";
    private static final String OPERATOR_ROLE = OPERATOR_RESOURCE_BASE_URL + "/role.yaml";
    private static final String OPERATOR_ROLE_BINDING = OPERATOR_RESOURCE_BASE_URL + "/role_binding.yaml";
    private static final String OPERATOR_DEPLOYMENT = OPERATOR_RESOURCE_BASE_URL + "/operator.yaml";

    private static final String KOGITO_OPERATOR_NAME = "kogito-cloud-operator";
    private static final String KOGITO_CRD_NAME = "kogitoapps.app.kiegroup.org";

    /**
     * Deploy Kogito Operator into specific project.
     * Currently there is no clear procedure how to deploy the operator with specific image tag from CLI or REST API using subscription, deploying it using available yaml files. 
     *
     * @param project Project where operator will be deployed to.
     * @return Fabric8 client for Kogito operator.
     */
    public static NonNamespaceOperation<KogitoApp, KogitoAppList, KogitoAppDoneable, Resource<KogitoApp, KogitoAppDoneable>> deployKogitoOperator(Project project) {
        OpenShiftBinary masterBinary = OpenShifts.masterBinary(project.getName());
        createServiceAccountInProject(project);
        createRoleInProject(project, masterBinary);
        createRoleBindingsInProject(project, masterBinary);
        createOperatorInProject(project);

        return getKogitoOperatorFabric8Client(project);
    }

    private static void createServiceAccountInProject(Project project) {
        logger.info("Creating service account in project '" + project.getName() + "' from " + OPERATOR_SERVICE_ACCOUNT);
        ServiceAccount serviceAccount = project.getMaster().serviceAccounts().load(getResource(OPERATOR_SERVICE_ACCOUNT)).get();
        project.getMaster().createServiceAccount(serviceAccount);
    }

    private static void createRoleInProject(Project project, OpenShiftBinary masterBinary) {
        logger.info("Creating role in project '" + project.getName() + "' from " + OPERATOR_ROLE);
        masterBinary.execute("create", "-f", OPERATOR_ROLE);
    }

    private static void createRoleBindingsInProject(Project project, OpenShiftBinary masterBinary) {
        logger.info("Creating role bindings in project '" + project.getName() + "' from " + OPERATOR_ROLE_BINDING);
        masterBinary.execute("create", "-f", OPERATOR_ROLE_BINDING);
    }

    private static void createOperatorInProject(Project project) {
        logger.info("Creating operator in project '" + project.getName() + "' from " + OPERATOR_DEPLOYMENT);
        Deployment deployment = project.getMaster().apps().deployments().load(getResource(OPERATOR_DEPLOYMENT)).get();
        project.getMaster().apps().deployments().create(deployment);

        // wait until operator is ready
        project.getMaster().waiters().areExactlyNPodsRunning(1, "name", KOGITO_OPERATOR_NAME).waitFor();
    }

    private static URL getResource(String resourceUrl) {
        try {
            return new URL(resourceUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL for resource: " + resourceUrl, e);
        }
    }

    private static NonNamespaceOperation<KogitoApp, KogitoAppList, KogitoAppDoneable, Resource<KogitoApp, KogitoAppDoneable>> getKogitoOperatorFabric8Client(Project project) {
        CustomResourceDefinition customResourceDefinition = project.getMaster().customResourceDefinitions().withName(KOGITO_CRD_NAME).get();
        return project.getMaster().customResources(customResourceDefinition, KogitoApp.class, KogitoAppList.class, KogitoAppDoneable.class).inNamespace(project.getName());
    }
}
