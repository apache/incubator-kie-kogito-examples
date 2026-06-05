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
package org.kie.kogito.dmn.consumer.example;

import org.junit.jupiter.api.Test;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.dmn.consumer.example.customprofiles.CustomDMNProfile;

import io.quarkus.test.junit.QuarkusTest;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class TrafficViolationTest extends TrafficViolationTestBase {

    @jakarta.inject.Inject
    DecisionModels decisionModels;

    @Test
    void testCustomDMNProfile() {
        assertThat(decisionModels).isNotNull();
        DecisionModel decisionModel = decisionModels.getDecisionModel("https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF", "Traffic Violation");
        assertThat(decisionModel).isNotNull().isInstanceOf(DmnDecisionModel.class);
        DmnDecisionModel dmnDecisionModel = (DmnDecisionModel) decisionModel;
        assertThat(dmnDecisionModel).isNotNull();
        assertThat(dmnDecisionModel.getProfiles()).anyMatch(profile -> profile instanceof CustomDMNProfile);
    }
}
