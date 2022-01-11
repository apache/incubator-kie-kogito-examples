/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.app;

import org.kie.kogito.drools.core.config.DefaultRuleEventListenerConfig;
import org.kie.kogito.examples.CustomRuleEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import io.micrometer.prometheus.PrometheusMeterRegistry;

@Configuration
public class RuleEventListenerConfig extends DefaultRuleEventListenerConfig {

    @Autowired
    public RuleEventListenerConfig(PrometheusMeterRegistry prometheusMeterRegistry) {
        super(new CustomRuleEventListener(prometheusMeterRegistry));
    }
}
