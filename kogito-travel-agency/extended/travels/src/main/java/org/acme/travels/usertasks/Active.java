package org.acme.travels.usertasks;

import java.util.Arrays;
import java.util.List;

import org.kie.kogito.process.workitem.LifeCyclePhase;

public class Active implements LifeCyclePhase {

    public static final String ID = "active";
    public static final String STATUS = "Ready-KOGITO-3559";

    private List<String> allowedTransitions = Arrays.asList();

    public Active() {
    }

    public String id() {
        return "active";
    }

    public String status() {
        return "Ready-KOGITO-3559";
    }

    public boolean isTerminating() {
        return false;
    }

    public boolean canTransition(LifeCyclePhase phase) {
        return phase == null ? true : this.allowedTransitions.contains(phase.id());
    }
}
