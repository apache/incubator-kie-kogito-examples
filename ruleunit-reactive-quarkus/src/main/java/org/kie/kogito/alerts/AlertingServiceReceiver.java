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

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.kie.kogito.rules.DataStream;
import org.kie.kogito.rules.RuleUnitInstance;

@ApplicationScoped
public class AlertingServiceReceiver {

    private DataStream<Event> eventData;

    // Maybe we will put this in a registry but hold this for this example
    private RuleUnitInstance<org.kie.kogito.alerts.AlertingService> ruleUnitInstance;

    public DataStream<Event> getEventData() {
        return eventData;
    }

    public void setEventData(DataStream<Event> eventData) {
        this.eventData = eventData;
    }

    public RuleUnitInstance<org.kie.kogito.alerts.AlertingService> getRuleUnitInstance() {
        return ruleUnitInstance;
    }

    public void setRuleUnitInstance(RuleUnitInstance<org.kie.kogito.alerts.AlertingService> ruleUnitInstance) {
        this.ruleUnitInstance = ruleUnitInstance;
    }

    @Incoming("events")
    public void receive(Event event) throws InterruptedException {
        System.out.println("AlertingServiceReceiver receive : " + event);
        if (eventData == null || ruleUnitInstance == null) {
            System.out.println("*** not yet initialized. Skip!");
            return;
        }
        eventData.append(event);
        ruleUnitInstance.fire();
    }
}
