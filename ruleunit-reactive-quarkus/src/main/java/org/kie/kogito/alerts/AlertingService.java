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

import org.kie.kogito.rules.DataProcessor;
import org.kie.kogito.rules.DataStream;
import org.kie.kogito.rules.RuleUnitData;

public class AlertingService implements RuleUnitData {

    private DataStream<Event> eventData;
    private DataStream<Alert> alertData;

    private AlertingServiceEmitter emitter;
    private AlertingServiceReceiver receiver;

    public AlertingService() {
    }

    public AlertingService(DataStream<Event> eventData, DataStream<Alert> alertData) {
        this.eventData = eventData;
        this.alertData = alertData;
    }

    public AlertingService(AlertingServiceEmitter emitter, AlertingServiceReceiver receiver) {
        this.emitter = emitter;
        this.receiver = receiver;
    }

    public void setEventData(DataStream<Event> eventData) {
        this.eventData = eventData;
        if (receiver != null) {
            receiver.setEventData(eventData);
        }
    }

    public DataStream<Event> getEventData() {
        return eventData;
    }

    public DataStream<Alert> getAlertData() {
        return alertData;
    }

    public void setAlertData(DataStream<Alert> alertData) {
        if (emitter != null) {
            this.alertData = new DataStream<Alert>() {

                @Override
                public void subscribe(DataProcessor<Alert> subscriber) {
                    alertData.subscribe(subscriber);
                }

                @Override
                public void append(Alert value) {
                    emitter.send(value); // for outgoing channel
                    alertData.append(value); // for in-memory ksession. We may comment out this if we use this service only for reactive incoming/outgoing (no query endpoint)
                }
            };
        } else {
            this.alertData = alertData;
        }
    }

    @Override()
    public String toString() {
        return "AlertingService" + "( " + "eventData=" + eventData + ", " + "alertData=" + alertData + " )";
    }
}
