package org.kie.kogito.examples;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.kie.addons.monitoring.process.PrometheusProcessEventListener;

@ApplicationScoped
public class ProcessEventListenerConfig extends DefaultProcessEventListenerConfig {

    public ProcessEventListenerConfig() {
        super(new PrometheusProcessEventListener("acme-travels"));
    }
}