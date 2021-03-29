/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;
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
public class ScoreCardTest {

    private static final String BASE_PATH = "/SampleScore";
    private static final String TARGET = "overallScore";

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testEvaluateScoreCardResult() {
        String inputData = "{\"age\": 23.0, \"occupation\": \"SKYDIVER\", \"residenceState\": \"AP\", \"validLicense\": true}";
        testResult(inputData, BASE_PATH, TARGET, 21.345f);
    }

    @Test
    public void testEvaluateScoreCardResultWrongData() {
        String inputData = "{\"age\": wrong-data, \"occupation\": \"SKYDIVER\", \"residenceState\": \"AP\", \"validLicense\": true}";
        testResultWrongData(inputData, BASE_PATH);
    }

    @Test
    public void testEvaluateScoreCardResultDescriptive() {
        String inputData = "{\"age\": 23.0, \"occupation\": \"SKYDIVER\", \"residenceState\": \"AP\", \"validLicense\": true}";
        final Map<String, Object> expectedResultMap = Collections.singletonMap(TARGET, 21.345f);
        testDescriptive(inputData, BASE_PATH, TARGET, expectedResultMap);
    }

    @Test
    public void testEvaluateScoreCardResultDescriptiveWrongData() {
        String inputData = "{\"age\": wrong-data, \"occupation\": \"SKYDIVER\", \"residenceState\": \"AP\", \"validLicense\": true}";
        testDescriptiveWrongData(inputData, BASE_PATH);
    }
}
