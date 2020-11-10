package org.acme.sw.onboarding.helpers;

import java.time.LocalDate;
import java.time.Period;

public final class DateHelper {

    private DateHelper() {

    }

    public static int calculateAge(final LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
