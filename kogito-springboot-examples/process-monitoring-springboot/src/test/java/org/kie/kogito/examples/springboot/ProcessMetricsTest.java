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
package org.kie.kogito.examples.springboot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abort;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@AutoConfigureMetrics
public class ProcessMetricsTest {

    private static final String PROJECT_VERSION = ProjectMetadataProvider.getProjectVersion();
    private static final String PROJECT_ARTIFACT_ID = ProjectMetadataProvider.getProjectArtifactId();

    // restassured needs to know the random port created for test
    @LocalServerPort
    int port;

    @Autowired
    @Qualifier("demo.orders")
    Process<? extends Model> orderProcess;

    @Autowired
    @Qualifier("demo.orderItems")
    Process<? extends Model> orderItemsProcess;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        // need it when running with persistence
        abort(orderProcess.instances());
        abort(orderItemsProcess.instances());
    }

    @Test
    public void testProcessMetricsSpringboot() {
        assertNotNull(orderProcess);

        // test adding new order
        String addOrderPayload = "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        String firstCreatedId = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(addOrderPayload).when()
                .post("/orders")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .extract().path("id");

        // test getting the created order
        given().accept(ContentType.JSON).when().get("/orders").then().statusCode(200)
                .body("size()", is(1), "[0].id", is(firstCreatedId));

        given()
                .when()
                .get("/actuator/prometheus")
                .then()
                .statusCode(200)
                .body(containsString(
                        String.format("kogito_process_instance_running_total{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"demo.orders\",version=\"%s\",} 1.0",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format("kogito_process_instance_started_total{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"demo.orderItems\",version=\"%s\",} 1.0",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format("kogito_process_instance_running_total{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"demo.orders\",version=\"%s\",} 1.0",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format("kogito_process_instance_running_total{app_id=\"default-process-monitoring-listener\",artifactId=\"%s\",process_id=\"demo.orderItems\",version=\"%s\",} 1.0",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format(
                                "kogito_work_item_duration_seconds_max{artifactId=\"%s\",name=\"org.kie.kogito.examples.springboot.CalculationService_calculateTotal_3_Handler\",version=\"%s\",}",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format(
                                "kogito_work_item_duration_seconds_count{artifactId=\"%s\",name=\"org.kie.kogito.examples.springboot.CalculationService_calculateTotal_3_Handler\",version=\"%s\",}",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)))
                .body(containsString(
                        String.format(
                                "kogito_work_item_duration_seconds_sum{artifactId=\"%s\",name=\"org.kie.kogito.examples.springboot.CalculationService_calculateTotal_3_Handler\",version=\"%s\",}",
                                PROJECT_ARTIFACT_ID, PROJECT_VERSION)));
    }
}
