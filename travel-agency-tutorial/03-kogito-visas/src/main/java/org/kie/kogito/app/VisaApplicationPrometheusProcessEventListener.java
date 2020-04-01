package org.kie.kogito.app;

import org.acme.travels.VisaApplication;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.addons.monitoring.process.PrometheusProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;

public class VisaApplicationPrometheusProcessEventListener extends PrometheusProcessEventListener {
	
	protected final Counter numberOfVisaApplicationsApproved = Counter.build()
            .name("acme_travels_visas_approved_total")
            .help("Approved visa applications")
            .labelNames("app_id", "country", "duration", "nationality")
            .register();
	
	protected final Counter numberOfVisaApplicationsRejected = Counter.build()
            .name("acme_travels_visas_rejected_total")
            .help("Rejected visa applications")
            .labelNames("app_id", "country", "duration", "nationality")
            .register();

	private String identifier;
	
	public VisaApplicationPrometheusProcessEventListener(String identifier) {
		super(identifier);
		this.identifier = identifier;
	}
	
	public void cleanup() {
		CollectorRegistry.defaultRegistry.unregister(numberOfVisaApplicationsApproved);
		CollectorRegistry.defaultRegistry.unregister(numberOfVisaApplicationsRejected);
	}

	@Override
	public void afterProcessCompleted(ProcessCompletedEvent event) {
		super.afterProcessCompleted(event);
		final WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl) event.getProcessInstance();
		
		if (processInstance.getProcessId().equals("visaApplications")) {
			VisaApplication application = (VisaApplication) processInstance.getVariable("visaApplication");
		
			if (application.isApproved()) {
				numberOfVisaApplicationsApproved.labels(identifier, safeValue(application.getCountry()), String.valueOf(application.getDuration()), safeValue(application.getNationality())).inc();
			} else {
				numberOfVisaApplicationsRejected.labels(identifier, safeValue(application.getCountry()), String.valueOf(application.getDuration()), safeValue(application.getNationality())).inc();
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
