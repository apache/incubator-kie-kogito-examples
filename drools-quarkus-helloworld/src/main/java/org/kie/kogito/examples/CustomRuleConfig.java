package org.kie.kogito.examples;

import javax.inject.Singleton;

import org.drools.core.config.DefaultRuleEventListenerConfig;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.kogito.rules.RuleConfig;
import org.kie.kogito.rules.RuleEventListenerConfig;

/**
 * This is an example custom configuration. You can delete this file entirely
 * if you want just the default settings to be picked up.
 */
@Singleton
public class CustomRuleConfig implements RuleConfig {
    @Override
    public RuleEventListenerConfig ruleEventListeners() {
        return new DefaultRuleEventListenerConfig();
    }

    @Override
    public EventProcessingOption eventProcessingMode() {
        return EventProcessingOption.CLOUD;
    }

    @Override
    public ClockTypeOption clockType() {
        return ClockTypeOption.REALTIME;
    }
}
