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

import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.rbac.Role;
import io.fabric8.kubernetes.api.model.rbac.RoleBinding;
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
    private static final String OPERATOR_CUSTOM_RESOURCE_DEFINITION = OPERATOR_RESOURCE_BASE_URL + "/crds/app_v1alpha1_kogitoapp_crd.yaml";
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
        createCustomResourceDefinitionsInOpenShift(project);
        createServiceAccountInProject(project);
        createRoleInProject(project);
        createRoleBindingsInProject(project);
        createOperatorInProject(project);

        return getKogitoOperatorFabric8Client(project);
    }

    private static void createCustomResourceDefinitionsInOpenShift(Project project) {
        CustomResourceDefinition customResourceDefinition = project.getAdmin().customResourceDefinitions().withName(KOGITO_CRD_NAME).get();
        if (customResourceDefinition == null) {
            logger.info("Creating custom resource definition '" + KOGITO_CRD_NAME + "' from " + OPERATOR_CUSTOM_RESOURCE_DEFINITION);
            customResourceDefinition = project.getAdmin().customResourceDefinitions().load(getResource(OPERATOR_CUSTOM_RESOURCE_DEFINITION)).get();
            project.getAdmin().customResourceDefinitions().create(customResourceDefinition);
        }
    }

    private static void createServiceAccountInProject(Project project) {
        logger.info("Creating service account in project '" + project.getName() + "' from " + OPERATOR_SERVICE_ACCOUNT);
        ServiceAccount serviceAccount = project.getAdmin().serviceAccounts().load(getResource(OPERATOR_SERVICE_ACCOUNT)).get();
        project.getAdmin().createServiceAccount(serviceAccount);
    }

    private static void createRoleInProject(Project project) {
        logger.info("Creating role in project '" + project.getName() + "' from " + OPERATOR_ROLE);
        Role role = project.getAdmin().rbac().roles().load(getResource(OPERATOR_ROLE)).get();
        project.getAdmin().rbac().roles().inNamespace(project.getName()).create(role);
    }

    private static void createRoleBindingsInProject(Project project) {
        logger.info("Creating role bindings in project '" + project.getName() + "' from " + OPERATOR_ROLE_BINDING);
        RoleBinding roleBinding = project.getAdmin().rbac().roleBindings().load(getResource(OPERATOR_ROLE_BINDING)).get();
        project.getAdmin().rbac().roleBindings().inNamespace(project.getName()).create(roleBinding);
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
        CustomResourceDefinition customResourceDefinition = project.getAdmin().customResourceDefinitions().withName(KOGITO_CRD_NAME).get();
        return project.getAdmin().customResources(customResourceDefinition, KogitoApp.class, KogitoAppList.class, KogitoAppDoneable.class).inNamespace(project.getName());
    }
}
