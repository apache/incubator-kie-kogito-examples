package org.acme.travels.usertasks;

import java.util.Arrays;
import java.util.List;

import org.jbpm.process.instance.impl.workitem.Complete;
import org.kie.kogito.process.workitem.LifeCyclePhase;

/**
 * Extension to Complete life cycle phase that applies to any human task.
 * It will set the status to "Completed" 
 * 
 * This phase will only allow to complete tasks that are in started phase.
 *
 * It can transition from
 * <ul>
 *  <li>Start</li>
 * </ul>
 * 
 * This is a terminating (final) phase.
 */
public class CompleteStartedOnly extends Complete {

    private List<String> allowedTransitions = Arrays.asList(Start.ID);
    
    @Override
    public boolean canTransition(LifeCyclePhase phase) {
        return allowedTransitions.contains(phase.id());        
    }

}
