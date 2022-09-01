package org.kie.kogito.examples.sw.custom;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.process.impl.CachedWorkItemHandlerConfig;


@ApplicationScoped
public class RPCCustomWorkItemHandlerConfig extends CachedWorkItemHandlerConfig {
    
    @Inject
    RPCCustomWorkItemHandler handler;
    
    @PostConstruct
    void init () {
        register(handler.getName(),handler);
    }
}
