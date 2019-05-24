package org.kie.kogito.examples;

import org.drools.core.config.DefaultRuleEventListenerConfig;
import org.kie.addons.monitoring.rule.PrometheusMetricsDroolsListener;


public class RuleEventListenerConfig extends DefaultRuleEventListenerConfig {

    
    public RuleEventListenerConfig() {
        super(new PrometheusMetricsDroolsListener("onboarding"));
    }
}
