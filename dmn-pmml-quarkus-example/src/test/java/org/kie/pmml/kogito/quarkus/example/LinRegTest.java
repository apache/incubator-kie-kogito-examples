/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.pmml.kogito.quarkus.example;

import java.util.Collections;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.pmml.kogito.quarkus.example.CommonTestUtils.testDescriptive;
import static org.kie.pmml.kogito.quarkus.example.CommonTestUtils.testDescriptiveWrongData;
import static org.kie.pmml.kogito.quarkus.example.CommonTestUtils.testResult;
import static org.kie.pmml.kogito.quarkus.example.CommonTestUtils.testResultWrongData;

@QuarkusTest
public class LinRegTest {

    private static final String BASE_PATH = "/LinReg";
    private static final String TARGET = "fld4";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testEvaluateLinRegResult() {
        String inputData = "{\"fld1\":3.0, \"fld2\":2.0, \"fld3\":\"y\"}";
        testResult(inputData, BASE_PATH, TARGET, 52.5f);
    }

    @Test
    void testEvaluateLinRegResultWrongData() {
        String inputData = "{\"fld1\":\"a\", \"fld2\":2, \"fld3\":\"y\"}";
        testResultWrongData(inputData, BASE_PATH);
    }

    @Test
    void testEvaluateLinRegDescriptive() {
        String inputData = "{\"fld1\":3.0, \"fld2\":2.0, \"fld3\":\"y\"}";
        final Map<String, Object> expectedResultMap = Collections.singletonMap(TARGET, 52.5f);
        testDescriptive(inputData, BASE_PATH, TARGET, expectedResultMap);
    }

    @Test
    void testEvaluateLinRegDescriptiveWrongData() {
        String inputData = "{\"fld1\":\"a\", \"fld2\":2, \"fld3\":\"y\"}";
        testDescriptiveWrongData(inputData, BASE_PATH);
    }
}
