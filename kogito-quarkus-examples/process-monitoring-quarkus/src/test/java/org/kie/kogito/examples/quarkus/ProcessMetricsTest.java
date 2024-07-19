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
package org.kie.kogito.examples.quarkus;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.examples.quarkus.demo.Order;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abort;

@QuarkusTest
public class ProcessMetricsTest {

    private static final String PROJECT_VERSION = ProjectMetadataProvider.getProjectVersion();
    private static final String PROJECT_ARTIFACT_ID = ProjectMetadataProvider.getProjectArtifactId();

    @Inject
    @Named("demo.orders")
    Process<? extends Model> orderProcess;

    @Inject
    @Named("demo.orderItems")
    Process<? extends Model> orderItemsProcess;

    @BeforeEach
    public void setup() {
        // abort all instances after each test
        // as other tests might have added instances
        // needed until Quarkus implements @DirtiesContext similar to springboot
        // see https://github.com/quarkusio/quarkus/pull/2866
        abort(orderProcess.instances());
        abort(orderItemsProcess.instances());
    }

    @Test
    public void testProcessMetricsQuarkus() {
        assertNotNull(orderProcess);

        Model m = orderProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("approver", "john");
        parameters.put("order", new Order("12345", false, 0.0));
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = orderProcess.createInstance(m);
        processInstance.start();

        given()
                .when()
                .get("/q/metrics")
                .then()
                .statusCode(200)
                .body(containsString(
                        String.format("kogito_process_instance_running_total{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"demo.orders\",version=\"%s\"} 1.0",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format("kogito_process_instance_started_total{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"demo.orderItems\",version=\"%s\"} 1.0",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format("kogito_process_instance_running_total{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"demo.orders\",version=\"%s\"} 1.0",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format("kogito_process_instance_running_total{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"demo.orderItems\",version=\"%s\"} 1.0",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format(
                                "kogito_work_item_duration_seconds_max{artifactId=\"%s\",name=\"org.kie.kogito.examples.quarkus.CalculationService_calculateTotal_ServiceTask_1_Handler\",version=\"%s\"}",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format(
                                "kogito_work_item_duration_seconds_count{artifactId=\"%s\",name=\"org.kie.kogito.examples.quarkus.CalculationService_calculateTotal_ServiceTask_1_Handler\",version=\"%s\"}",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format(
                                "kogito_work_item_duration_seconds_sum{artifactId=\"%s\",name=\"org.kie.kogito.examples.quarkus.CalculationService_calculateTotal_ServiceTask_1_Handler\",version=\"%s\"}",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)));
    }
}
