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
package org.kie.kogito.alerts;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;

import io.quarkus.runtime.Startup;

@ApplicationScoped
@Startup
public class AlertingServiceReactiveExecutor {

    @Inject
    RuleUnit<AlertingService> ruleUnit;

    @Inject
    AlertingServiceEmitter emitter;

    @Inject
    AlertingServiceReceiver receiver;

    @PostConstruct
    void onPostConstruct() {
        System.out.println("AlertingServiceReactiveExecutor : onPostConstruct");

        // This is only one stateful RuleUnit in this service with the special reactive DataStream
        AlertingService unitData = new AlertingService(emitter, receiver);
        RuleUnitInstance<org.kie.kogito.alerts.AlertingService> ruleUnitInstance = ruleUnit.createInstance(unitData);
        receiver.setRuleUnitInstance(ruleUnitInstance);
    }
}
