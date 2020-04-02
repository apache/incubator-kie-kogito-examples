package org.kie.kogito.app;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;

@ApplicationScoped
public class ProcessEventListenerConfig extends DefaultProcessEventListenerConfig {
   
	private VisaApplicationPrometheusProcessEventListener listener;
    public ProcessEventListenerConfig() {
        super();
    }
    
    @PostConstruct
    public void setup() {
    	this.listener = new VisaApplicationPrometheusProcessEventListener("acme-travels");
    	register(this.listener);
    }
    
    @PreDestroy
    public void close() { 
    	
    	this.listener.cleanup();
    }
}
