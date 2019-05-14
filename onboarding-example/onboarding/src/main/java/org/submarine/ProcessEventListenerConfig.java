package org.submarine;

import org.kie.addons.monitoring.process.PrometheusProcessEventListener;
import org.kie.submarine.process.impl.DefaultProcessEventListenerConfig;


public class ProcessEventListenerConfig extends DefaultProcessEventListenerConfig {
   
    public ProcessEventListenerConfig() {
        super(new PrometheusProcessEventListener("onboarding"));
    }
}
