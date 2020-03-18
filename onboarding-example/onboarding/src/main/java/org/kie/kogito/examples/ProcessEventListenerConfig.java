package org.kie.kogito.examples;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.monitoring.process.PrometheusProcessEventListener;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;

@ApplicationScoped
public class ProcessEventListenerConfig extends DefaultProcessEventListenerConfig {
   
    public ProcessEventListenerConfig() {
        super(new PrometheusProcessEventListener("onboarding"));
    }
}
