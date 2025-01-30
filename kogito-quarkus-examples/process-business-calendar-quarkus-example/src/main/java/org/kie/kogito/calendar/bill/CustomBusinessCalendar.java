package org.kie.kogito.calendar.bill;

import java.util.Date;

import org.kie.kogito.calendar.BusinessCalendar;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomBusinessCalendar implements BusinessCalendar {
    @Override
    public long calculateBusinessTimeAsDuration(String s) {
        return 0;
    }

    @Override
    public Date calculateBusinessTimeAsDate(String s) {
        return null;
    }
}
