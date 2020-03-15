package org.acme.travels;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.kie.kogito.tests.KogitoInfinispanSpringbootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = KogitoInfinispanSpringbootApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class BoundaryTimersProcessTest {

    @Autowired
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

        latch.await(1, TimeUnit.MINUTES);

        Optional<? extends ProcessInstance<?>> exists = timersOnTaskProcess.instances().findById(processInstance.id());
        assertFalse(exists.isPresent());
    }
}
