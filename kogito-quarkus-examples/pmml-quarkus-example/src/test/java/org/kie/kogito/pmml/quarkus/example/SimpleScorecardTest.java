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
package org.kie.kogito.pmml.quarkus.example;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

import static org.kie.kogito.pmml.quarkus.example.CommonTestUtils.testDescriptive;
import static org.kie.kogito.pmml.quarkus.example.CommonTestUtils.testResult;

@QuarkusTest
class SimpleScorecardTest {

    private static final String BASE_PATH = "/Testscorecard/SimpleScorecard";
    private static final String TARGET = "score";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testEvaluateSimpleScorecardResult() {
        String inputData = "{\"input1\":5.0, \"input2\":-10.0}";
        testResult(inputData, BASE_PATH, TARGET, -15.0f);
    }

    @Test
    void testEvaluateSimpleScorecardDescriptive() {
        String inputData = "{\"input1\":5.0, \"input2\":-10.0}";
        final Map<String, Object> expectedResultMap = new HashMap<>();
        expectedResultMap.put(TARGET, -15.0f);
        expectedResultMap.put("Reason Code 1", "Input1ReasonCode");
        expectedResultMap.put("Reason Code 2", "Input2ReasonCode");
        testDescriptive(inputData, BASE_PATH, TARGET, expectedResultMap);
    }

}
