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

package org.kie.kogito.examples.sw.opentelemetry.jaeger.helper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import com.fasterxml.jackson.databind.JsonNode;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.jboss.logging.Logger;

import org.awaitility.Awaitility;

/**
 * Helper methods for Jaeger trace JSON. 
 * Jaeger trace format: GET /api/traces/{traceId} 
 * Response: { "data": [{"traceID": "...", "spans": [...] } ] }
 */
public final class JaegerHelper {

    private static final Logger LOGGER = Logger.getLogger(JaegerHelper.class);
    private static final String DEBUG_JAEGER_PROP = "test.jaeger.debug";

    // Must match application.properties: quarkus.otel.service.name
    public static final String SERVICE_NAME = "sw-opentelemetry-jaeger-example";
    public static final String JAEGER_QUERY_BASE_URL_KEY = "test.jaeger.query.base-url";

    public static final Duration TIMEOUT = Duration.ofSeconds(30);
    public static final Duration POLL = Duration.ofMillis(500);

    private JaegerHelper() {
    }

    public static JsonNode extractFirstTrace(JsonNode traceResponse) {
        JsonNode data = traceResponse.get("data");
        if (data == null || !data.isArray() || data.isEmpty()) {
            throw new AssertionError("Expected Jaeger response to contain non-empty 'data' array");
        }
        return data.get(0);
    }

    public static void assertHasAnySpans(JsonNode jaegerTrace) {
        JsonNode spans = jaegerTrace.get("spans");
        if (spans == null || !spans.isArray() || spans.isEmpty()) {
            throw new AssertionError("Expected trace to contain non-empty 'spans' array");
        }
    }

    public static String assertAnyWorkflowSpanHasTag(JsonNode trace, String key) {
        return assertAnyWorkflowSpanHasTag(trace, key, null, false);
    }

    public static String assertAnyWorkflowSpanHasTag(JsonNode trace, String key, String expectedValue) {
        return assertAnyWorkflowSpanHasTag(trace, key, expectedValue, true);
    }

    public static void assertHasAtLeastOneHttpServerSpan(JsonNode trace) {
        JsonNode spans = trace.get("spans");
        if (spans == null || !spans.isArray() || spans.isEmpty()) {
            throw new AssertionError("No spans found in trace");
        }

        for (JsonNode span : spans) {
            String op = span.hasNonNull("operationName") ? span.get("operationName").asText() : "";
            if (op.startsWith("POST ") || op.startsWith("GET ") || op.startsWith("PUT ") || op.startsWith("DELETE ")) {
                return;
            }
            // Alternative: check for span.kind=server tag
            if (spanHasTag(span, "span.kind", "server")) {
                return;
            }
        }

        throw new AssertionError(
                "Did not find any HTTP server span (operationName starting with HTTP verb or span.kind=server)");
    }

    public static void assertWorkflowStatesContainAtLeast(JsonNode trace, String... expectedStates) {
        Set<String> states = collectWorkflowStates(trace);
        for (String s : expectedStates) {
            if (!states.contains(s)) {
                throw new AssertionError("Expected workflow states to contain '" + s + "', but states were: " + states);
            }
        }
    }

    public static JsonNode assertWorkflowHasAnyState(JsonNode trace, String state) {
        return findWorkflowSpanByState(trace, state)
                .orElseThrow(() -> new AssertionError("Did not find workflow span with state '" + state + "'"));
    }

    public static String assertSpanHasTag(JsonNode span, String tagKey) {
        return findTagValue(span, tagKey)
                .orElseThrow(() -> new AssertionError("Expected span to contain tag '" + tagKey + "'"));
    }

    public static Optional<String> findTagValue(JsonNode span, String tagKey) {
        JsonNode tags = span.get("tags");
        if (tags == null || !tags.isArray()) {
            return Optional.empty();
        }

        for (JsonNode tag : tags) {
            if (tagKey.equals(tag.path("key").asText())) {
                return Optional.ofNullable(tag.path("value").asText(null));
            }
        }
        return Optional.empty();
    }

    public static Set<String> collectWorkflowStates(JsonNode trace) {
        Set<String> states = new HashSet<>();
        JsonNode spans = trace.get("spans");
        if (spans == null || !spans.isArray()) {
            return states;
        }

        for (JsonNode span : spans) {
            String op = span.hasNonNull("operationName") ? span.get("operationName").asText() : "";
            if (!op.startsWith("sonataflow.process.")) {
                continue;
            }

            JsonNode tags = span.get("tags");
            if (tags == null || !tags.isArray()) {
                continue;
            }

            for (JsonNode tag : tags) {
                String key = tag.hasNonNull("key") ? tag.get("key").asText() : "";
                if ("sonataflow.workflow.state".equals(key)) {
                    String val = tag.hasNonNull("value") ? tag.get("value").asText() : null;
                    if (val != null && !val.isBlank()) {
                        states.add(val);
                    }
                }
            }
        }
        return states;
    }

    public static Optional<JsonNode> findWorkflowSpanByState(JsonNode trace, String expectedState) {
        JsonNode spans = trace.get("spans");
        if (spans == null || !spans.isArray()) {
            return Optional.empty();
        }

        for (JsonNode span : spans) {
            if (!isWorkflowSpan(span)) {
                continue;
            }
            Optional<String> state = findTagValue(span, "sonataflow.workflow.state");
            if (state.isPresent() && expectedState.equals(state.get())) {
                return Optional.of(span);
            }
        }
        return Optional.empty();
    }

    public static boolean isWorkflowSpan(JsonNode span) {
        if (span == null) {
            return false;
        }

        JsonNode op = span.get("operationName");
        if (op != null && op.isTextual() && op.asText().startsWith("sonataflow.process.")) {
            return true;
        }

        JsonNode tags = span.get("tags");
        if (tags == null || !tags.isArray()) {
            return false;
        }

        for (JsonNode tag : tags) {
            JsonNode keyNode = tag.get("key");
            if (keyNode != null && keyNode.isTextual()) {
                String key = keyNode.asText();
                if (key.startsWith("sonataflow.")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static JsonNode assertAnySpanOperationNameEquals(JsonNode trace, String expectedOperationName) {
        JsonNode spans = trace.get("spans");
        if (spans == null || !spans.isArray()) {
            throw new AssertionError("Trace JSON missing spans array");
        }

        for (JsonNode span : spans) {
            String op = span.path("operationName").asText("");
            if (expectedOperationName.equals(op)) {
                return span;
            }
        }

        throw new AssertionError("Did not find any span with operationName='" + expectedOperationName + "'");
    }

    public static String requireTagValue(JsonNode span, String key) {
        JsonNode tags = span.get("tags");
        if (tags == null || !tags.isArray()) {
            throw new AssertionError("Span JSON missing tags array");
        }

        for (JsonNode tag : tags) {
            if (key.equals(tag.path("key").asText())) {
                String value = tag.path("value").asText(null);
                if (value == null || value.isBlank()) {
                    throw new AssertionError("Tag '" + key + "' value is empty");
                }
                return value;
            }
        }
        throw new AssertionError("Did not find tag '" + key + "' on span");
    }

    public static void assertAnySpanLooksErrored(JsonNode jaegerTrace) {
        JsonNode spans = jaegerTrace.get("spans");
        if (spans == null || !spans.isArray()) {
            throw new AssertionError("Trace JSON missing spans array");
        }
        for (JsonNode span : spans) {
            JsonNode tags = span.get("tags");
            if (tags == null || !tags.isArray()) {
                continue;
            }
            for (JsonNode tag : tags) {
                String key = tag.hasNonNull("key") ? tag.get("key").asText() : "";
                if ("error".equalsIgnoreCase(key)) {
                    JsonNode v = tag.get("value");
                    if (v != null && (v.asBoolean(false) || "true".equalsIgnoreCase(v.asText()))) {
                        return;
                    }
                }
                if (key.toLowerCase().contains("exception") || key.toLowerCase().contains("status")) {
                    return;
                }
            }
        }
        throw new AssertionError("Did not find any span with an error indicator tag (e.g., error=true)");
    }

    public static void triggerWorkflow(String endpoint, String body, String transactionId, String correlationId) {
        given().header("X-TRANSACTION-ID", transactionId)
	       .header("X-TRACKER-correlation_id", correlationId)
               .contentType("application/json")
	       .body(body)
	.when()
	       .post(endpoint).then()
               .statusCode(anyOf(is(200), is(201)));
    }

    public static JsonNode waitForTraceWithTracker(String trackerKey, String trackerValue) {
        return waitForTraceWithTracker(trackerKey, trackerValue, null, new String[0]);
    }

    public static JsonNode waitForTraceWithTracker(String trackerKey, String trackerValue,
            String expectedProcessInstanceId, String... requiredStates) {
        JaegerQueryClient jaeger = new JaegerQueryClient(getJaegerBaseUrl());

        Awaitility.await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofMillis(500))
                .until(() -> jaeger.listServices().contains(SERVICE_NAME));

        return Awaitility.await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofMillis(500)).until(() -> {
            // Look at multiple recent traces, not only "latest"
            List<String> traceIds = jaeger.findTraceIdsForService(SERVICE_NAME, 30);
            if (traceIds.isEmpty()) {
                return null;
            }

            for (String traceId : traceIds) {
                JsonNode resp = jaeger.getTrace(traceId); // {data:[{...}]}
                JsonNode data = resp.get("data");
                if (data == null || !data.isArray() || data.isEmpty()) {
                    continue;
                }
                JsonNode trace = data.get(0);

                // Must have tracker on a workflow span
                if (!containsWorkflowSpanTag(trace, trackerKey, trackerValue)) {
                    continue;
                }

                // If PID is provided, require it too
                if (expectedProcessInstanceId != null && !expectedProcessInstanceId.isBlank()
                        && !containsWorkflowSpanTag(trace, "sonataflow.process.instance.id",
                                expectedProcessInstanceId)) {
                    continue;
                }

                // If states are required, check that the trace contains them
                if (requiredStates != null && requiredStates.length > 0) {
                    Set<String> states = collectWorkflowStates(trace);
                    boolean allPresent = true;
                    for (String s : requiredStates) {
                        if (!states.contains(s)) {
                            allPresent = false;
                            break;
                        }
                    }
                    if (!allPresent) {
                        continue;
                    }
                }

                debugPrintTrace(trace);
                return trace;
            }
            return null;
        }, t -> t != null);
    }

    private static boolean containsWorkflowSpanTag(JsonNode trace, String key, String expectedValue) {
        JsonNode spans = trace.get("spans");
        if (spans == null || !spans.isArray()) {
            return false;
        }

        for (JsonNode span : spans) {
            String op = span.hasNonNull("operationName") ? span.get("operationName").asText() : "";
            if (!op.startsWith("sonataflow.process.")) {
                continue;
            }
            JsonNode tags = span.get("tags");
            if (tags == null || !tags.isArray()) {
                continue;
            }
            for (JsonNode tag : tags) {
                String k = tag.hasNonNull("key") ? tag.get("key").asText() : "";
                if (!key.equals(k))
                    continue;
                String v = tag.hasNonNull("value") ? tag.get("value").asText() : "";
                if (expectedValue.equals(v))
                    return true;
            }
        }
        return false;
    }

    private static String getJaegerBaseUrl() {
        String url = System.getProperty(JAEGER_QUERY_BASE_URL_KEY);
        if (url == null || url.isBlank()) {
            url = System.getenv("TEST_JAEGER_QUERY_BASE_URL");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalStateException(
                    "Jaeger base URL not configured. Expected system property: " + JAEGER_QUERY_BASE_URL_KEY);
        }
        return url;
    }

    private static String assertAnyWorkflowSpanHasTag(JsonNode trace, String key, String expectedValue,
            boolean checkValue) {
        JsonNode spans = trace.get("spans");
        if (spans == null || !spans.isArray()) {
            throw new AssertionError("Trace JSON missing spans array");
        }

        for (JsonNode span : spans) {
            String op = span.hasNonNull("operationName") ? span.get("operationName").asText() : "";

            if (!op.startsWith("sonataflow.process.")) {
                continue;
            }

            JsonNode tags = span.get("tags");
            if (tags == null || !tags.isArray()) {
                continue;
            }

            for (JsonNode tag : tags) {
                String k = tag.hasNonNull("key") ? tag.get("key").asText() : "";
                if (!key.equals(k)) {
                    continue;
                }

                String actualValue = tag.hasNonNull("value") ? tag.get("value").asText() : null;

                if (!checkValue) {
                    if (actualValue == null || actualValue.isBlank()) {
                        throw new AssertionError("Tag '" + key + "' found but value is empty");
                    }
                    return actualValue;
                }

                if (expectedValue.equals(actualValue)) {
                    return actualValue;
                }
            }
        }

        if (checkValue) {
            throw new AssertionError("Did not find workflow span tag '" + key + "' with value '" + expectedValue + "'");
        }

        throw new AssertionError("Did not find workflow span tag '" + key + "' on any workflow span");
    }

    private static boolean spanHasTag(JsonNode span, String expectedKey, String expectedValue) {
        JsonNode tags = span.get("tags");
        if (tags == null || !tags.isArray()) {
            return false;
        }
        for (JsonNode tag : tags) {
            String key = tag.hasNonNull("key") ? tag.get("key").asText() : "";
            if (!expectedKey.equals(key)) {
                continue;
            }
            String val = tag.hasNonNull("value") ? tag.get("value").asText() : "";
            if (expectedValue == null || expectedValue.equals(val)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isJaegerDebugEnabled() {
        // -Dtest.jaeger.debug=true
        String v = System.getProperty(DEBUG_JAEGER_PROP);
        if (v == null || v.isBlank()) {
            v = System.getenv("TEST_JAEGER_DEBUG");
        }
        return "true".equalsIgnoreCase(v) || "1".equals(v) || "yes".equalsIgnoreCase(v);
    }

    public static void debugPrintTrace(JsonNode trace) {
        if (!isJaegerDebugEnabled()) {
            return;
        }

        LOGGER.info("=== Span names ===");
        for (JsonNode span : trace.get("spans")) {
            LOGGER.info("span.operationName=" + span.get("operationName").asText());
        }

        LOGGER.info("=== Tags containing process/sonataflow/transaction/tracker ===");
        for (JsonNode span : trace.get("spans")) {
            JsonNode tags = span.get("tags");
            if (tags == null || !tags.isArray()) {
                continue;
            }

            String op = span.hasNonNull("operationName") ? span.get("operationName").asText() : "<unknown>";
            for (JsonNode tag : tags) {
                String key = tag.hasNonNull("key") ? tag.get("key").asText() : "";
                if (key.contains("process") || key.contains("sonataflow") || key.contains("transaction")
                        || key.contains("tracker")) {
                    LOGGER.info(op + " :: " + tag.toPrettyString());
                }
            }
        }
    }

}
