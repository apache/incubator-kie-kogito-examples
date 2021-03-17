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
package org.kie.pmml.kogito.springboot.example;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.RestAssured;

import static org.kie.pmml.kogito.springboot.example.CommonTestUtils.testDescriptive;
import static org.kie.pmml.kogito.springboot.example.CommonTestUtils.testDescriptiveWrongData;
import static org.kie.pmml.kogito.springboot.example.CommonTestUtils.testResult;
import static org.kie.pmml.kogito.springboot.example.CommonTestUtils.testResultWrongData;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MiningModelTest {

    private static final String BASE_PATH = "/PredicatesMining";
    private static final String TARGET = "categoricalResult";

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    void testEvaluatePredicatesMiningResult() {
        String inputData = "{\"residenceState\":\"AP\", " +
                "\"validLicense\":true, " +
                "\"occupation\":\"ASTRONAUT\", " +
                "\"categoricalY\":\"classA\", " +
                "\"categoricalX\":\"red\", " +
                "\"variable\":6.6, " +
                "\"age\":25.0}";
        testResult(inputData, BASE_PATH, TARGET, 1.381666666666666f);
    }

    @Test
    void testEvaluatePredicatesMiningResultWrongData() {
        String inputData = "{\"residenceState\":\"AP\", " +
                "\"validLicense\":true, " +
                "\"occupation\":\"ASTRONAUT\", " +
                "\"categoricalY\":\"classA\", " +
                "\"categoricalX\":\"red\", " +
                "\"variable\":6.6, " +
                "\"age\":\"b\"}";
        testResultWrongData(inputData, BASE_PATH);
    }

    @Test
    void testEvaluatePredicatesMiningDescriptive() {
        String inputData = "{\"residenceState\":\"AP\", " +
                "\"validLicense\":true, " +
                "\"occupation\":\"ASTRONAUT\", " +
                "\"categoricalY\":\"classA\", " +
                "\"categoricalX\":\"red\", " +
                "\"variable\":6.6, " +
                "\"age\":25.0}";
        final Map<String, Object> expectedResultMap = Collections.singletonMap(TARGET, 1.381666666666666f);
        testDescriptive(inputData, BASE_PATH, TARGET, expectedResultMap);
    }

    @Test
    void testEvaluatePredicatesMiningDescriptiveWrongData() {
        String inputData = "{\"residenceState\":\"AP\", " +
                "\"validLicense\":true, " +
                "\"occupation\":\"ASTRONAUT\", " +
                "\"categoricalY\":\"classA\", " +
                "\"categoricalX\":\"red\", " +
                "\"variable\":6.6, " +
                "\"age\":\"b\"}";
        testDescriptiveWrongData(inputData, BASE_PATH);
    }
}
