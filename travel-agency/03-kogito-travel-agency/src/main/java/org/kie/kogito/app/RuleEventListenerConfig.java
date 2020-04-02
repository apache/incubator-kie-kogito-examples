package org.kie.kogito.app;

import javax.enterprise.context.ApplicationScoped;

import org.drools.core.config.DefaultRuleEventListenerConfig;
import org.kie.addons.monitoring.rule.PrometheusMetricsDroolsListener;

@ApplicationScoped
public class RuleEventListenerConfig extends DefaultRuleEventListenerConfig {

    
    public RuleEventListenerConfig() {
        super(new PrometheusMetricsDroolsListener("acme-travels"));
    }
}
