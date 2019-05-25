package org.kie.kogito.examples;

import org.kie.addons.monitoring.process.PrometheusProcessEventListener;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;


public class ProcessEventListenerConfig extends DefaultProcessEventListenerConfig {
   
    public ProcessEventListenerConfig() {
        super(new PrometheusProcessEventListener("onboarding"));
    }
}
