package org.kie.kogito.app;

import javax.inject.Singleton;

import org.drools.core.config.DefaultRuleEventListenerConfig;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessEventListenerConfig;
import org.kie.kogito.process.WorkItemHandlerConfig;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;
import org.kie.kogito.rules.RuleConfig;
import org.kie.kogito.rules.RuleEventListenerConfig;
import org.kie.kogito.signal.SignalManagerHub;
import org.kie.kogito.uow.UnitOfWorkManager;

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
            public EventProcessingOption eventProcessingMode() {
                return EventProcessingOption.CLOUD;
            }

            @Override
            public ClockTypeOption clockType() {
                return ClockTypeOption.REALTIME;
            }
        };
    }
}
