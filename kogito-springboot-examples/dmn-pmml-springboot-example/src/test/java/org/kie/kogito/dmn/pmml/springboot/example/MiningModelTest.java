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
package org.kie.kogito.dmn.pmml.springboot.example;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.RestAssured;

import static org.kie.kogito.dmn.pmml.springboot.example.CommonTestUtils.testDescriptive;
import static org.kie.kogito.dmn.pmml.springboot.example.CommonTestUtils.testDescriptiveWrongData;
import static org.kie.kogito.dmn.pmml.springboot.example.CommonTestUtils.testResult;
import static org.kie.kogito.dmn.pmml.springboot.example.CommonTestUtils.testResultWrongData;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MiningModelTest {

    private static final String BASE_PATH = "/Testminingmodelsummed/MiningModelSum";
    private static final String TARGET = "result";

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    void testEvaluateMiningModelResult() {
        String inputData = "{\"input1\":200.0, \"input2\":-1.0, \"input3\":2.0}";
        testResult(inputData, BASE_PATH, TARGET, -299.0f);
    }

    @Test
    void testEvaluateMiningModelResultWrongData() {
        String inputData = "{\"input1\":wrong-data, \"input2\":-1.0, \"input3\":2.0}";
        testResultWrongData(inputData, BASE_PATH);
    }

    @Test
    void testEvaluateMiningModelResultDescriptive() {
        String inputData = "{\"input1\":200.0, \"input2\":-1.0, \"input3\":2.0}";
        final Map<String, Object> expectedResultMap = Collections.singletonMap(TARGET, -299.0f);
        testDescriptive(inputData, BASE_PATH, TARGET, expectedResultMap);
    }

    @Test
    void testEvaluateMiningModelResultDescriptiveWrongData() {
        String inputData = "{\"input1\":wrong-data, \"input2\":-1.0, \"input3\":2.0}";
        testDescriptiveWrongData(inputData, BASE_PATH);
    }
}
