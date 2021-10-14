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
import io.quarkus.scheduler.Scheduled;

@Startup
@ApplicationScoped
public class Adaptor {

    @Inject
    RuleUnit<AlertingService> ruleUnit;
    @Inject
    AlertingService alertingService;

    RuleUnitInstance<AlertingService> ruleUnitInstance;

    @PostConstruct
    void init() {
        this.ruleUnitInstance = ruleUnit.createInstance(alertingService);
        // I am not 100% sure of this because I don't know if this subscription will come before or after the other
        // I'd assume _after_ because AlertingService must be initialized first
        //        this.alertingService.getEventData().subscribe(DataObserver.of(incoming -> ruleUnitInstance.fire()));
    }

    // alternatively, we can set a timer!
    @Scheduled(every = "10s")
    void fire() {
        ruleUnitInstance.fire();
    }

}
