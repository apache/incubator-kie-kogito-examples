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
package org.kie.dmnpmml.kogito.springboot.example;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.RestAssured;

import static org.kie.dmnpmml.kogito.springboot.example.CommonTestUtils.testDescriptive;
import static org.kie.dmnpmml.kogito.springboot.example.CommonTestUtils.testDescriptiveWrongData;
import static org.kie.dmnpmml.kogito.springboot.example.CommonTestUtils.testResult;
import static org.kie.dmnpmml.kogito.springboot.example.CommonTestUtils.testResultWrongData;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DecisionTreeTest {

    private static final String BASE_PATH = "/DecisionTree";
    private static final String TARGET = "decision";

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    void testEvaluateDecisionTreeResult() {
        String inputData = "{\"temperature\":30.0, \"humidity\":10.0}";
        testResult(inputData, BASE_PATH, TARGET, "sunglasses");
    }

    @Test
    void testEvaluateDecisionTreeResultWrongData() {
        String inputData = "{\"temperature\":\"b\", \"humidity\":10.0}";
        testResultWrongData(inputData, BASE_PATH);
    }

    @Test
    void testEvaluateDecisionTreeDescriptive() {
        String inputData = "{\"temperature\":30.0, \"humidity\":10.0}";
        final Map<String, Object> expectedResultMap = new HashMap<>();
        expectedResultMap.put(TARGET, "sunglasses");
        expectedResultMap.put("weatherdecision", "sunglasses");
        testDescriptive(inputData, BASE_PATH, TARGET, expectedResultMap);
    }

    @Test
    void testEvaluateDecisionTreeDecriptiveWrongData() {
        String inputData = "{\"temperature\":\"b\", \"humidity\":10.0}";
        testDescriptiveWrongData(inputData, BASE_PATH);
    }
}
