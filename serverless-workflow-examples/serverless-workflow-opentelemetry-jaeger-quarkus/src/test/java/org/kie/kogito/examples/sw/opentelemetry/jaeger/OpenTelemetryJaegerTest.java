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

package org.kie.kogito.examples.sw.opentelemetry.jaeger;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import org.awaitility.Awaitility;

import io.quarkus.test.common.QuarkusTestResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.kie.kogito.examples.sw.opentelemetry.jaeger.helper.JaegerPoller;
import org.kie.kogito.examples.sw.opentelemetry.jaeger.helper.JaegerQueryClient;
import static org.kie.kogito.examples.sw.opentelemetry.jaeger.helper.JaegerHelper.*;

@QuarkusTestResource(JaegerTestResource.class)
public abstract class OpenTelemetryJaegerTest {

    private static final String GREET_ENDPOINT = "/greet";
    private static final String ERROR_ENDPOINT = "/error";

    /**
     * Test 1: traces exist and contain SonataFlow tags + transaction/tracker tags
     */
    @Test
    void test1_tracesExistAndContainSonataflowTags() {
        triggerWorkflow(GREET_ENDPOINT, "{\"name\":\"Ada\"}", "txn-001", "corr-123");

        JsonNode trace = waitForTraceWithTracker("sonataflow.tracker.correlation_id", "corr-123");

        assertHasAnySpans(trace);

        String processInstanceId = assertAnyWorkflowSpanHasTag(trace, "sonataflow.process.instance.id");
        assertFalse(processInstanceId.isBlank());
        assertAnyWorkflowSpanHasTag(trace, "sonataflow.process.id", "greet");
        assertAnyWorkflowSpanHasTag(trace, "sonataflow.tracker.correlation_id", "corr-123");
        assertAnyWorkflowSpanHasTag(trace, "sonataflow.transaction.id", "txn-001");

        assertHasAtLeastOneHttpServerSpan(trace);
        assertWorkflowStatesContainAtLeast(trace, "Init", "CallSubflow", "Finish", "Done");
    }

    /**
     * Test 2: fallback when no X-TRANSACTION-ID header: transaction tag sets to process.instance.id
     */
    @Test
    void test2_transactionFallbackWhenHeaderMissing() {
        // No transaction header
        given().header("X-TRACKER-correlation_id", "corr-fallback-1")
	       .contentType("application/json")
               .body("{\"name\":\"Edu\"}")
	.when()
	       .post(GREET_ENDPOINT)
	.then()
	       .statusCode(anyOf(is(200), is(201)));

        JsonNode trace = waitForTraceWithTracker("sonataflow.tracker.correlation_id", "corr-fallback-1");

        String processInstanceId = assertAnyWorkflowSpanHasTag(trace, "sonataflow.process.instance.id");
        assertFalse(processInstanceId.isBlank());

        // transactiond.id is sent with processInstanceId
        assertAnyWorkflowSpanHasTag(trace, "sonataflow.transaction.id", processInstanceId);
    }

    /**
     * Test 3: subflow correlation Parent workflow triggers subflow. 
     * Validate tracker + transaction are present
     * (correlation across parent/subflow)
     */
    @Test
    void test3_subflowPlaceholderStateIsTracedAndCorrelated() {
        given().header("X-TRANSACTION-ID", "txn-subflow-001")
	       .header("X-TRACKER-testcase", "t3-subflow")
               .contentType("application/json")
	       .body("{\"name\":\"Ada\"}")
	.when()
	       .post(GREET_ENDPOINT).then()
               .statusCode(anyOf(is(200), is(201)));

        JsonNode trace = waitForTraceWithTracker("sonataflow.tracker.testcase", "t3-subflow");

        assertWorkflowStatesContainAtLeast(trace, "Init", "CallSubflow", "Done");

        assertAnyWorkflowSpanHasTag(trace, "sonataflow.transaction.id", "txn-subflow-001");

        String pid = assertAnyWorkflowSpanHasTag(trace, "sonataflow.process.instance.id");
        assertFalse(pid.isBlank());

        JsonNode callSubflowSpan = assertWorkflowHasAnyState(trace, "CallSubflow");
        assertSpanHasTag(callSubflowSpan, "sonataflow.transaction.id");

        JsonNode parentSpan = assertAnySpanOperationNameEquals(trace, "sonataflow.process.greet.execute");
        JsonNode subflowSpan = assertAnySpanOperationNameEquals(trace, "sonataflow.process.greetSubflow.execute");

        String parentTx = requireTagValue(parentSpan, "sonataflow.transaction.id");
        String subflowTx = requireTagValue(subflowSpan, "sonataflow.transaction.id");

        assertEquals(parentTx, subflowTx, "Transaction id must match between parent and subflow spans");
    }

    /**
     * Test 4: error workflow shows error
     */
    @Test
    void test4_errorWorkflowShowsError() {
        given().header("X-TRANSACTION-ID", "txn-error-001")
	       .header("X-TRACKER-testcase", "t4-error")
               .contentType("application/json")
	       .body("{\"name\":\"Soul\"}")
	.when()
	       .post(ERROR_ENDPOINT).then()
               .statusCode(anyOf(is(400), is(500)));

        JsonNode trace = waitForTraceWithTracker("sonataflow.tracker.testcase", "t4-error");

        assertAnySpanLooksErrored(trace);
        assertAnyWorkflowSpanHasTag(trace, "sonataflow.transaction.id", "txn-error-001");
    }

}
