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

package org.kie.kogito.examples.sw.opentelemetry.jaeger.persistence;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.Optional;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.kie.kogito.examples.sw.opentelemetry.jaeger.JaegerTestResource;
import org.kie.kogito.examples.sw.opentelemetry.jaeger.helper.JaegerPoller;
import org.kie.kogito.examples.sw.opentelemetry.jaeger.helper.JaegerQueryClient;
import static org.kie.kogito.examples.sw.opentelemetry.jaeger.helper.JaegerHelper.*;

@QuarkusIntegrationTest
@QuarkusTestResource(PostgresTestResource.class)
@QuarkusTestResource(JaegerTestResource.class)
public class PersistWait02ResumeIT {

    private static final String SERVICE_NAME = "sw-opentelemetry-jaeger-example";
    private static final String PROCESS_ID = "persistWait";
    private static final String BASE = "/persistWait";

    private static final Duration TIMEOUT = Duration.ofSeconds(60);
    private static final Duration POLL = Duration.ofMillis(500);

    @Test
    void resume_after_restart_should_continue_and_trace_has_context() {
        String pid = PersistWaitPidStore.read();

        //instance must still exist
        given().when()
		      .get(BASE + "/" + pid)
	       .then()
	              .statusCode(200);

        // Send resume CloudEvent
        given().contentType("application/cloudevents+json")
	       .body(CloudEvents.resumeEventWithProcessInstanceId(pid))
        .when()
	       .post("/resume")
	.then()
	       .statusCode(is(202));

        // Verify via Jaeger that we got spans for this process instance (after resuming)
        JaegerQueryClient jaeger = new JaegerQueryClient(getJaegerBaseUrl());

        JaegerPoller.waitForService(jaeger, SERVICE_NAME, TIMEOUT, POLL);

        // Poll until a trace appears that contains our processInstanceId tag
        String traceId = Awaitility.await().atMost(TIMEOUT).pollInterval(POLL)
                .until(() -> findTraceIdByProcessInstanceId(jaeger, SERVICE_NAME, pid), Optional::isPresent).get();

        JsonNode traceResponse = jaeger.getTrace(traceId);

        JsonNode trace = waitForTraceWithTracker("sonataflow.tracker.correlation_id", "corr-persist-001", pid,
                "waitForEvent", "finish");

        debugPrintTrace(trace);

        assertAnyWorkflowSpanHasTag(trace, "sonataflow.process.id", PROCESS_ID);
        assertAnyWorkflowSpanHasTag(trace, "sonataflow.process.instance.id", pid);

        assertWorkflowStatesContainAtLeast(trace, "waitForEvent", "finish");
    }

    private static Optional<String> findTraceIdByProcessInstanceId(JaegerQueryClient jaeger, String serviceName,
            String pid) {
        // Read latest traces and scan them for a span tag sonataflow.process.instance.id == pid
        JsonNode traces = jaeger.findTracesByService(serviceName, 20);

        JsonNode data = traces.get("data");
        if (data == null || !data.isArray() || data.isEmpty()) {
            return Optional.empty();
        }

        for (JsonNode t : data) {
            String traceId = t.hasNonNull("traceID") ? t.get("traceID").asText() : null;
            if (traceId == null || traceId.isBlank()) {
                continue;
            }

            JsonNode full = jaeger.getTrace(traceId);
            JsonNode trace = extractFirstTrace(full);
            try {
                assertAnyWorkflowSpanHasTag(trace, "sonataflow.process.instance.id", pid);
                return Optional.of(traceId);
            } catch (AssertionError e) {
            }

        }
        return Optional.empty();
    }

    private static String getJaegerBaseUrl() {
        String key = "test.jaeger.query.base-url";
        String url = System.getProperty(key);
        if (url == null || url.isBlank()) {
            url = System.getenv("TEST_JAEGER_QUERY_BASE_URL");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Jaeger base URL not configured. Expected system property: " + key);
        }
        return url;
    }
}
