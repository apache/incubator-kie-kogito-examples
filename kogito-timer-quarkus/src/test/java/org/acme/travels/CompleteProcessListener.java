package org.acme.travels;

import java.util.concurrent.CountDownLatch;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;

public class CompleteProcessListener extends DefaultProcessEventListener {

    private CountDownLatch latch;

    public CompleteProcessListener(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        latch.countDown();
    }
}

