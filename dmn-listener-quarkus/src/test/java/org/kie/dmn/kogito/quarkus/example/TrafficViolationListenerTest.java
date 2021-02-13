/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.kogito.quarkus.example;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.kogito.quarkus.example.mock.MockDMNRuntimeEventListener;
import org.kie.kogito.decision.DecisionConfig;
import org.kie.kogito.decision.DecisionEventListenerConfig;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.dmn.kogito.quarkus.example.TrafficViolationTest.TRAFFIC_VIOLATION_TEST_BODY;

@QuarkusTest
public class TrafficViolationListenerTest {

    @Inject
    DecisionConfig decisionConfig;

    @Test
    public void testEvaluateTrafficViolation() {
        List<DMNRuntimeEventListener> injectedListeners = Optional.ofNullable(decisionConfig)
                .map(DecisionConfig::decisionEventListeners)
                .map(DecisionEventListenerConfig::listeners)
                .orElseThrow(() -> new IllegalStateException("Can't find injected listeners"));

        assertEquals(4, injectedListeners.size());

        MockDMNRuntimeEventListener testListener = injectedListeners.stream()
                .filter(MockDMNRuntimeEventListener.class::isInstance)
                .findFirst()
                .map(MockDMNRuntimeEventListener.class::cast)
                .orElseThrow(() -> new IllegalStateException("Can't find injected MockDMNRuntimeEventListener"));

        testListener.reset();

        given().body(TRAFFIC_VIOLATION_TEST_BODY).contentType(ContentType.JSON).post("/Traffic Violation");

        Map<String, Integer> testListenerCalls = testListener.getCalls();
        assertTrue(testListenerCalls.containsKey("beforeEvaluateAll"));
        assertEquals(1, testListenerCalls.get("beforeEvaluateAll"));
        assertTrue(testListenerCalls.containsKey("afterEvaluateAll"));
        assertEquals(1, testListenerCalls.get("afterEvaluateAll"));
    }

}
