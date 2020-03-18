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
public class BoundaryTimersProcessTest {

    @Inject
    Application app;

    @Test
    public void testTimersProcess() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        ((DefaultProcessEventListenerConfig) app.config().process().processEventListeners()).register(new CompleteProcessListener(latch));

        Process<? extends Model> timersOnTaskProcess = app.processes().processById("timersOnTask");
        assertNotNull(timersOnTaskProcess);
        Model m = timersOnTaskProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("delay", "PT3S");
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = timersOnTaskProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());

        latch.await(2, TimeUnit.MINUTES);

        Optional<? extends ProcessInstance<?>> exists = timersOnTaskProcess.instances().findById(processInstance.id());
        assertFalse(exists.isPresent());
    }
}
