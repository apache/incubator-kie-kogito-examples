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
package org.acme.travels;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class TimersProcessTest {

    @Inject
    Application app;

    @Test
    public void testTimersProcess() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        app.config().process().processEventListeners().listeners().add(new CompleteProcessListener(latch));

        Process<? extends Model> timersProcess = app.processes().processById("timers");
        assertNotNull(timersProcess);
        Model m = timersProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("delay", "PT3S");
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = timersProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());

        latch.await(1, TimeUnit.MINUTES);

        Optional<? extends ProcessInstance<?>> exists = timersProcess.instances().findById(processInstance.id());
        assertFalse(exists.isPresent());
    }
}
