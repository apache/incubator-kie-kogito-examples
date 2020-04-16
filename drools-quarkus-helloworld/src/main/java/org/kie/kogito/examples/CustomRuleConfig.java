/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.examples;

import javax.inject.Singleton;

import org.drools.core.config.DefaultRuleEventListenerConfig;
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
}
