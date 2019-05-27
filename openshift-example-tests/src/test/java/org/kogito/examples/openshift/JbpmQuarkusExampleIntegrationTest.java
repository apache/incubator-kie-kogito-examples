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

import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kogito.examples.openshift.Project;
import org.kogito.examples.openshift.TestConfig;
import org.kogito.examples.openshift.deployment.Deployer;
import org.kogito.examples.openshift.deployment.HttpDeployment;

public class JbpmQuarkusExampleIntegrationTest {

    private static Project project;
    private static HttpDeployment kaasDeloyment;

    @BeforeClass
    public static void setUp() throws MalformedURLException {
        URL assetsUrl = new URL("https://github.com/kiegroup/kogito-examples");
        String gitContextDir = "jbpm-quarkus-example";

        String randomProjectName = RandomStringUtils.randomAlphanumeric(4).toLowerCase();
        project = Project.create("jbpm-example-" + randomProjectName);
        kaasDeloyment = Deployer.deployKaasUsingS2iAndWait(project, assetsUrl, gitContextDir, TestConfig.getKaasS2iQuarkusBuilderImage(), TestConfig.getKaasQuarkusRuntimeImage());
    }

    @AfterClass
    public static void tearDown() {
        project.delete();
    }

    @Test
    public void testOrdersCrud() {
      RestAssured.given()
          .header("Content-Type", "application/json")
          .body("{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}")
      .when()
          .post(kaasDeloyment.getRouteUrl().toExternalForm() + "/orders")
      .then()
          .statusCode(200);
    }
}
