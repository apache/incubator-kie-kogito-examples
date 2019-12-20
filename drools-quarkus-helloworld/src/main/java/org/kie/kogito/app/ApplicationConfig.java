package org.kie.kogito.app;

import javax.inject.Singleton;

import org.drools.core.config.DefaultRuleEventListenerConfig;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.rules.ClockType;
import org.kie.kogito.rules.EventProcessingMode;
import org.kie.kogito.rules.RuleConfig;
import org.kie.kogito.rules.RuleEventListenerConfig;

/**
 * This is an example custom configuration. You can delete this file entirely
 * if you want just the default settings to be picked up.
 */
@Singleton
public class ApplicationConfig  implements org.kie.kogito.Config {

    @Override
    public ProcessConfig process() {
        return null;
    }

    @Override
    public RuleConfig rule() {
        return new RuleConfig() {
            @Override
            public RuleEventListenerConfig ruleEventListeners() {
                return new DefaultRuleEventListenerConfig();
            }

            @Override
            public EventProcessingMode eventProcessingMode() {
                return EventProcessingMode.Cloud;
            }

            @Override
            public ClockType clockType() {
                return ClockType.RealTime;
            }
        };
    }
}
