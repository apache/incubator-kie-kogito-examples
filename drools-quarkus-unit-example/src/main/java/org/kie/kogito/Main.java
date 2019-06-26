/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito;

import org.kie.kogito.rules.alerting.Event;
import org.kie.kogito.rules.alerting.LoggerService;
import org.kie.kogito.rules.alerting.LoggerServiceRuleUnit;
import org.kie.kogito.rules.alerting.MonitoringService;
import org.kie.kogito.rules.alerting.MonitoringServiceRuleUnit;

public class Main {

    public static void main(String[] args) {

        org.kie.kogito.examples.Application application = new org.kie.kogito.examples.Application();

        MonitoringService monitoringService = new MonitoringService();
        LoggerService loggerService = new LoggerService(monitoringService.getAlertStream());

        Executor executor = Executor.create();

        executor.submit(new MonitoringServiceRuleUnit().createInstance(monitoringService));
//        executor.submit(new LoggerServiceRuleUnit().createInstance(loggerService));

        monitoringService.getEventStream().add(new Event("Hello!"));
        monitoringService.getEventStream().add(new Event("Hello Again!"));
        monitoringService.getEventStream().add(new Event("Hello 3!"));

    }
}
