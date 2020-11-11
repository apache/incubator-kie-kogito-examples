package org.acme.sw.onboarding.queries;

import java.util.ArrayList;

import org.acme.sw.onboarding.model.Doctor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.rules.DataObserver;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssignmentUnitPlainTest {
    @Test
    public void create() {
        AssignmentUnit assignmentUnit = new AssignmentUnit();
        ArrayList<Doctor> doctors = new ArrayList<>();
        assignmentUnit.getDoctors().subscribe(DataObserver.of(doctors::add));
        assertEquals(doctors.size(), 6);
    }

}