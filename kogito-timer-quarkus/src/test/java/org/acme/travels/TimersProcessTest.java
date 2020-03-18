package org.acme.travels;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;

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
        ((DefaultProcessEventListenerConfig) app.config().process().processEventListeners()).register(new CompleteProcessListener(latch));

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
