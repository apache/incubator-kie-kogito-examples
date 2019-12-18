package org.kie.kogito.examples;

import java.util.Collection;
import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;

import org.kie.addons.monitoring.rule.PrometheusMetricsDroolsListener;
import org.kie.kogito.rules.listeners.AgendaListener;
import org.kie.kogito.rules.listeners.DataSourceListener;

@ApplicationScoped
public class RuleEventListenerConfig implements org.kie.kogito.rules.RuleEventListenerConfig {

    private final PrometheusMetricsDroolsListener onboarding =
            new PrometheusMetricsDroolsListener("onboarding");

    @Override
    public Collection<AgendaListener> agendaListener() {
        return Collections.singleton(onboarding);
    }

    @Override
    public Collection<DataSourceListener> dataSourceListener() {
        return Collections.emptyList();
    }
}
