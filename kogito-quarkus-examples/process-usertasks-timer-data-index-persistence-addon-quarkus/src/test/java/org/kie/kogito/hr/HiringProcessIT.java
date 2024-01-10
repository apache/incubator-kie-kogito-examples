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
package org.kie.kogito.hr;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusIntegrationTest
public class HiringProcessIT {
    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testApprovalProcess() {
        String response = given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances{ id } }\" }")
                .when().post("/graphql")
                .then()
                .extract().response().asPrettyString();

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances{ id } }\" }")
                .when().post("/graphql")
                .then().statusCode(200)
                .body("data.ProcessInstances.size()", is(greaterThanOrEqualTo(0)));

        /*
         * assertNotNull(hiringProcess);
         * 
         * Model m = hiringProcess.createModel();
         * Map<String, Object> parameters = new HashMap<>();
         * parameters.put("candidate", new CandidateData("John", "Doe", "jdoe@example.com", 12, Arrays.asList("Java", "Kogito")));
         * m.fromMap(parameters);
         * 
         * ProcessInstance<?> processInstance = hiringProcess.createInstance(m);
         * processInstance.start();
         * assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());
         * 
         * SecurityPolicy policy = SecurityPolicy.of(IdentityProviders.of("jdoe", Arrays.asList("HR", "IT")));
         * 
         * List<WorkItem> workItems = processInstance.workItems(policy);
         * assertEquals(5, workItems.size());
         * Map<String, Object> results = new HashMap<>();
         * results.put("approve", true);
         * processInstance.completeWorkItem(workItems.get(0).getId(), results, policy);
         * 
         * processInstance.workItems(policy);
         * 
         * workItems = processInstance.workItems(policy);
         * assertEquals(1, workItems.size());
         * 
         * results.put("approve", false);
         * processInstance.completeWorkItem(workItems.get(0).getId(), results, policy);
         * assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
         * 
         * Model result = (Model) processInstance.variables();
         * assertEquals(3, result.toMap().size());
         * assertEquals(true, result.toMap().get("hr_approval"));
         * assertEquals(false, result.toMap().get("it_approval"));
         */
    }

    @Test
    public void testCandidateNotMeetingRequirements() {
        /*
         * assertNotNull(hiringProcess);
         * 
         * Model m = hiringProcess.createModel();
         * Map<String, Object> parameters = new HashMap<>();
         * parameters.put("candidate", new CandidateData("John", "Doe", "jdoe@example.com", 0, Collections.emptyList()));
         * m.fromMap(parameters);
         * 
         * ProcessInstance<?> processInstance = hiringProcess.createInstance(m);
         * processInstance.start();
         * assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());
         */
    }
}
