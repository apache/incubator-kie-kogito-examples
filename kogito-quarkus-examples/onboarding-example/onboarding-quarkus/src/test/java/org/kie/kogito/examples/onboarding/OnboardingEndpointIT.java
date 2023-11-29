/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.examples.onboarding;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.kie.kogito.examples.test.RecordedOutputWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.testcontainers.quarkus.InfinispanQuarkusTestResource;

import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@EnableKubernetesMockClient
@QuarkusTest
@QuarkusTestResource(value = InfinispanQuarkusTestResource.Conditional.class)
public class OnboardingEndpointIT {

    @Inject
    ProcessConfig processConfig;

    @Test
    public void testOnboardingProcessUserAlreadyExists() {

        registerHandler("ValidateEmployee", (workitem) -> {

            Map<String, Object> results = new HashMap<>();
            results.put("status", "exists");
            results.put("message", "user already exists");
            return results;
        });

        given()
                .body("{\"employee\" : {\"firstName\" : \"Mark\", \"lastName\" : \"Test\", \"personalId\" : \"xxx-yy-zzz\", \"birthDate\" : \"1995-12-10T14:50:12.123+02:00\", \"address\" : {\"country\" : \"US\", \"city\" : \"Boston\", \"street\" : \"any street 3\", \"zipCode\" : \"10001\"}}}")
                .contentType(ContentType.JSON)
                .when()
                .post("/onboarding")
                .then()
                .statusCode(201)
                .body("status", is("exists"))
                .body("message", is("user already exists"));
    }

    @Test
    public void testOnboardingProcessNewUserUS() {
        ZonedDateTime paymentDate = ZonedDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        registerHandler("ValidateEmployee", (workitem) -> {

            Map<String, Object> results = new HashMap<>();
            results.put("status", "new");
            results.put("message", "user needs to be onboarded");
            return results;
        });
        registerHandler("AssignIdAndEmail", (workitem) -> {

            Map<String, Object> results = new HashMap<>();
            results.put("email", "test@company.com");
            results.put("employeeId", "acb123");
            return results;
        });
        registerHandler("AssignDepartmentAndManager", (workitem) -> {

            Map<String, Object> results = new HashMap<>();
            results.put("manager", "mary frog");
            results.put("department", "US00099");
            return results;
        });
        registerHandler("CalculatePaymentDate", (workitem) -> {

            Map<String, Object> results = new HashMap<>();
            results.put("paymentDate", Date.from(paymentDate.toInstant()));
            return results;
        });
        registerHandler("CalculateVacationDays", (workitem) -> {

            Map<String, Object> results = new HashMap<>();
            results.put("vacationDays", 25);
            return results;
        });
        registerHandler("CalculateTaxRate", (workitem) -> {

            Map<String, Object> results = new HashMap<>();
            results.put("taxRate", 22.0);
            return results;
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx");

        given()
                .body("{\"employee\" : {\"firstName\" : \"Mark\", \"lastName\" : \"Test\", \"personalId\" : \"xxx-yy-zzz\", \"birthDate\" : \"1995-12-10T14:50:12.123+02:00\", \"address\" : {\"country\" : \"US\", \"city\" : \"Boston\", \"street\" : \"any street 3\", \"zipCode\" : \"10001\"}}}")
                .contentType(ContentType.JSON)
                .when()
                .post("/onboarding")
                .then()
                .statusCode(201)
                .body("status", is("new"))
                .body("message", is("user needs to be onboarded"))
                .body("email", is("test@company.com"))
                .body("employeeId", is("acb123"))
                .body("manager", is("mary frog"))
                .body("department", is("US00099"))
                .body("payroll.paymentDate", is(paymentDate.format(formatter)))
                .body("payroll.vacationDays", is(25))
                .body("payroll.taxRate", is(Float.valueOf(22.0f)));
    }

    /*
     * Helper methods
     */
    protected void registerHandler(String name, Function<KogitoWorkItem, Map<String, Object>> item) {
        KogitoWorkItemHandler handler = processConfig.workItemHandlers().forName(name);
        ((RecordedOutputWorkItemHandler) handler).record(name, item);
    }
}
