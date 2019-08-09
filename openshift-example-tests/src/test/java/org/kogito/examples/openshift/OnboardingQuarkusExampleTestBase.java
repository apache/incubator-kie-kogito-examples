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

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kogito.examples.openshift.deployment.HttpDeployment;

public abstract class OnboardingQuarkusExampleTestBase {

    protected static Project project;

    protected static final String ASSETS_URL = "https://github.com/kiegroup/kogito-examples";
    protected static final String ONBOARDING_GIT_CONTEXT_DIR = "onboarding-example/onboarding";
    protected static final String PAYROLL_GIT_CONTEXT_DIR = "onboarding-example/payroll";
    protected static final String HR_GIT_CONTEXT_DIR = "onboarding-example/hr";

    protected static final String ONBOARDING_DEPLOYMENT_NAME = "onboarding";
    protected static final String PAYROLL_DEPLOYMENT_NAME = "payroll";
    protected static final String HR_DEPLOYMENT_NAME = "hr";

    @BeforeAll
    public static void setUpProject() throws MalformedURLException {
        String randomProjectName = RandomStringUtils.randomAlphanumeric(4).toLowerCase();
        project = Project.create("onboarding-example-" + randomProjectName);
    }

    @AfterAll
    public static void tearDownProject() {
        project.delete();
    }

    protected abstract HttpDeployment getKogitoDeployment();

    @Test
    public void testOnboarding() {
        onboardMarkTest().then()
                         .statusCode(200)
                         .assertThat().body(StringContains.containsString("\"manager\":\"John Doe\""), StringContains.containsString("\"taxRate\":35.0"));

        // Onboarding is still not yet finished, applicant is still in state "new".
        RestAssured.given()
            .header("Content-Type", "application/json")
        .when()
            .get(getOnboardingUrl())
        .then()
            .statusCode(200)
            .assertThat().body(StringContains.containsString("\"firstName\":\"Mark\""),
                               StringContains.containsString("\"lastName\":\"Test\""),
                               StringContains.containsString("\"message\":\"Employee Mark Test is not yet registered\""),
                               StringContains.containsString("\"status\":\"new\""));

        // Resend the same onboarding request, should return error message stating that onboarding of this person is already in progress
        onboardMarkTest().then()
                         .statusCode(200)
                         .assertThat().body(StringContains.containsString("\"message\":\"Employee Mark Test is already registered\""),
                                            StringContains.containsString("\"status\":\"exists\""));
    }

    private Response onboardMarkTest() {
        return RestAssured.given()
                              .header("Content-Type", "application/json")
                              .body("{\"employee\" : {\"firstName\" : \"Mark\", \"lastName\" : \"Test\", \"personalId\" : \"xxx-yy-zzz\", \"birthDate\" : \"1995-12-10T14:50:12.123+02:00\", \"address\" : {\"country\" : \"US\", \"city\" : \"Boston\", \"street\" : \"any street 3\", \"zipCode\" : \"10001\"}}}")
                          .when()
                              .post(getOnboardingUrl());
    }

    private String getOnboardingUrl() {
        return getKogitoDeployment().getRouteUrl().toExternalForm() + "/onboarding";
    }
}
