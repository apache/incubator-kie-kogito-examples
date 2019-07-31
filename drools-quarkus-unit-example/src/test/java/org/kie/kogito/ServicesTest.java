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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.kie.kogito.rules.DataObserver;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStream;
import org.kie.kogito.rules.alerting.Event;
import org.kie.kogito.rules.alerting.Logger;
import org.kie.kogito.rules.alerting.LoggerService;
import org.kie.kogito.rules.alerting.LoggerServiceRuleUnit;
import org.kie.kogito.rules.alerting.LoggerServiceRuleUnitInstance;
import org.kie.kogito.rules.alerting.MonitoringService;
import org.kie.kogito.rules.alerting.MonitoringServiceRuleUnit;
import org.kie.kogito.rules.alerting.MonitoringServiceRuleUnitInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServicesTest {

    @Test
    public void testExplicitly() {

        MonitoringService monitoringService = new MonitoringService();
        LoggerService loggerService = new LoggerService(monitoringService.getAlertStream());

        monitoringService.getEventStream().append(new Event("Hello!"));
        monitoringService.getEventStream().append(new Event("Hello Again!"));
        monitoringService.getEventStream().append(new Event("Hello 3!"));

        MonitoringServiceRuleUnitInstance monitoringServiceInstance =
                new MonitoringServiceRuleUnit().createInstance(monitoringService);
        monitoringServiceInstance.fire();

        monitoringService.getEventStream().append(new Event("Hello 4!"));
        monitoringServiceInstance.fire();

        LoggerServiceRuleUnitInstance loggerServiceInstance =
                new LoggerServiceRuleUnit().createInstance(loggerService);
        loggerServiceInstance.fire();

        CountDownLatch latch = new CountDownLatch(4);

        loggerService.getLogger().subscribe(DataObserver.of(v -> latch.countDown()));

        assertEquals(0, latch.getCount());
    }

    @Test
    public void testScheduled() throws InterruptedException {
        MonitoringService monitoringService = new MonitoringService();
        LoggerService loggerService = new LoggerService(monitoringService.getAlertStream());
        CountDownLatch latch = new CountDownLatch(4);
        loggerService.getLogger().subscribe(DataObserver.of(v -> latch.countDown()));

        MonitoringServiceRuleUnitInstance monitoringServiceInstance =
                new MonitoringServiceRuleUnit()
                        .createInstance(monitoringService);

        LoggerServiceRuleUnitInstance loggerServiceInstance =
                new LoggerServiceRuleUnit()
                        .createInstance(loggerService);

        Executor executor = Executor.create();
        executor.submit(monitoringServiceInstance);
        executor.submit(loggerServiceInstance);

        DataStream<Event> eventStream =
                monitoringService.getEventStream();

        eventStream.append(new Event("Hello!"));
        eventStream.append(new Event("Hello Again!"));
        eventStream.append(new Event("Hello 3!"));
        eventStream.append(new Event("Hello 4!"));

        latch.await(5, TimeUnit.SECONDS);

        assertEquals(0, latch.getCount(), "countdown must be zero");

    }
}
