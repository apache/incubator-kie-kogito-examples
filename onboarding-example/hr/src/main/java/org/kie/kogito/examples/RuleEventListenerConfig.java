package org.kie.kogito.examples;

import javax.enterprise.context.ApplicationScoped;

import org.kie.addons.monitoring.rule.PrometheusMetricsDroolsListener;
import org.kie.kogito.rules.listeners.AgendaListener;
import org.kie.kogito.rules.listeners.DataSourceListener;

@ApplicationScoped
public class RuleEventListenerConfig implements org.kie.kogito.rules.RuleEventListenerConfig {

    private final PrometheusMetricsDroolsListener onboarding =
            new PrometheusMetricsDroolsListener("onboarding");

    @Override
    public AgendaListener agendaListener() {
        return onboarding;
    }

    @Override
    public DataSourceListener dataSourceListener() {
        return null;
    }
}
