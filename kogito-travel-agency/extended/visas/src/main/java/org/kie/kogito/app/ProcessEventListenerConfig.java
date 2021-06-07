/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.KogitoGAV;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.monitoring.core.common.MonitoringRegistry;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;

@ApplicationScoped
public class ProcessEventListenerConfig extends DefaultProcessEventListenerConfig {

    private VisaApplicationPrometheusProcessEventListener listener;

    @Inject
    ConfigBean configBean;

    public ProcessEventListenerConfig() {
        super();
    }

    @PostConstruct
    public void setup() {
        this.listener = new VisaApplicationPrometheusProcessEventListener("acme-travels", configBean.getGav().orElse(KogitoGAV.EMPTY_GAV), MonitoringRegistry.getDefaultMeterRegistry());
        register(this.listener);
    }

    @PreDestroy
    public void close() {
        this.listener.cleanup();
    }
}
