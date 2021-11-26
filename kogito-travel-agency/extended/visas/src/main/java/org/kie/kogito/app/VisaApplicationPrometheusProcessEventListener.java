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

import java.util.Arrays;

import org.acme.travels.VisaApplication;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.monitoring.MonitoringRegistryManager;
import org.kie.kogito.monitoring.core.common.process.MetricsProcessEventListener;
import org.kie.kogito.monitoring.prometheus.api.PrometheusMeterRegistryManager;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;

public class VisaApplicationPrometheusProcessEventListener extends MetricsProcessEventListener {

    private static final String NUMBER_OF_VISA_APPROVED_COUNTER_NAME = "acme_travels_visas_approved_total";
    private static final String NUMBER_OF_VISA_REJECTED_COUNTER_NAME = "acme_travels_visas_rejected_total";

    private static Counter getNumberOfVisaApplicationsApprovedCounter(String appId, String country, String duration, String nationality, MonitoringRegistryManager monitoringRegistryManager) {
        return Counter
                .builder(NUMBER_OF_VISA_APPROVED_COUNTER_NAME)
                .description("Approved visa applications")
                .tags(Arrays.asList(Tag.of("app_id", appId), Tag.of("country", country), Tag.of("duration", duration), Tag.of("nationality", nationality)))
                .register(monitoringRegistryManager.getDefaultMeterRegistry());
    }

    private static Counter getNumberOfVisaApplicationsRejected(String appId, String country, String duration, String nationality, MonitoringRegistryManager monitoringRegistryManager) {
        return Counter
                .builder(NUMBER_OF_VISA_REJECTED_COUNTER_NAME)
                .description("Rejected visa applications")
                .tags(Arrays.asList(Tag.of("app_id", appId), Tag.of("country", country), Tag.of("duration", duration), Tag.of("nationality", nationality)))
                .register(monitoringRegistryManager.getDefaultMeterRegistry());
    }

    private String identifier;
    private MonitoringRegistryManager monitoringRegistryManager;
    private PrometheusMeterRegistryManager prometheusMeterRegistryManager;

    public VisaApplicationPrometheusProcessEventListener(String identifier, KogitoGAV kogitoGAV, MonitoringRegistryManager monitoringRegistryManager,
            PrometheusMeterRegistryManager prometheusMeterRegistryManager) {
        super(identifier, kogitoGAV, monitoringRegistryManager.getDefaultMeterRegistry());
        this.identifier = identifier;
        this.monitoringRegistryManager = monitoringRegistryManager;
        this.prometheusMeterRegistryManager = prometheusMeterRegistryManager;
    }

    public void cleanup() {
        prometheusMeterRegistryManager
                .getPrometheusMeterRegistry()
                .find(NUMBER_OF_VISA_APPROVED_COUNTER_NAME)
                .counters()
                .forEach(x -> ((CompositeMeterRegistry) monitoringRegistryManager.getDefaultMeterRegistry()).remove(x));
        prometheusMeterRegistryManager
                .getPrometheusMeterRegistry()
                .find(NUMBER_OF_VISA_REJECTED_COUNTER_NAME)
                .counters()
                .forEach(x -> ((CompositeMeterRegistry) monitoringRegistryManager.getDefaultMeterRegistry()).remove(x));
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        super.afterProcessCompleted(event);
        final WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl) event.getProcessInstance();

        if (processInstance.getProcessId().equals("visaApplications")) {
            VisaApplication application = (VisaApplication) processInstance.getVariable("visaApplication");

            if (application.isApproved()) {
                getNumberOfVisaApplicationsApprovedCounter(identifier, safeValue(application.getCountry()), String.valueOf(application.getDuration()),
                        safeValue(application.getNationality()), monitoringRegistryManager)
                                .increment();
            } else {
                getNumberOfVisaApplicationsRejected(identifier, safeValue(application.getCountry()), String.valueOf(application.getDuration()),
                        safeValue(application.getNationality()), monitoringRegistryManager)
                                .increment();
            }
        }
    }

    protected String safeValue(String value) {
        if (value == null) {
            return "unknown";
        }

        return value;
    }
}
