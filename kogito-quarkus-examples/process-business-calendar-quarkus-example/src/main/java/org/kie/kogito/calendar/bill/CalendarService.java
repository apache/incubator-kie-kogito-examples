package org.kie.kogito.calendar.bill;

import org.kie.kogito.calendar.BusinessCalendar;

import io.quarkus.runtime.StartupEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class CalendarService {
    @Inject
    BusinessCalendar businessCalendar;

    void onStart(@Observes StartupEvent ev) {
        System.out.println("CalendarService started");
        checkBusinessCalendar();
    }

    public void checkBusinessCalendar() {
        long time = businessCalendar.calculateBusinessTimeAsDuration("test");
        System.out.println("time: " + time + " from business calendar: " + businessCalendar.getClass().getName());
    }
}
