package org.kie.kogito.examples;

import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.springframework.stereotype.Component;
import org.kie.addons.monitoring.process.PrometheusProcessEventListener;

@Component
public class ProcessEventListenerConfig extends DefaultProcessEventListenerConfig {

    public ProcessEventListenerConfig() {
        super(new PrometheusProcessEventListener("acme-travels"));
    }
}