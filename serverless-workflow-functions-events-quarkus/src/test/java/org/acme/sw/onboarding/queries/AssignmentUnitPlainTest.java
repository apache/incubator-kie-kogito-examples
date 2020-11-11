package org.acme.sw.onboarding.queries;

import org.junit.jupiter.api.Test;
import org.kie.kogito.rules.DataObserver;


class AssignmentUnitPlainTest {
    @Test
    public void create() {
        AssignmentUnit assignmentUnit = new AssignmentUnit();
        assignmentUnit.getDoctors().subscribe(DataObserver.of(System.out::println));
    }

}