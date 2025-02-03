package org.kie.kogito.calendar.bill;

import org.kie.kogito.calendar.BusinessCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class CalendarService {
    @Inject
    BusinessCalendar businessCalendar;

    Logger logger = LoggerFactory.getLogger(CalendarService.class);

    void onStart(@Observes StartupEvent ev) {
        logger.info("CalendarService started");
        checkBusinessCalendar();
    }

    public void checkBusinessCalendar() {
        long time = businessCalendar.calculateBusinessTimeAsDuration("test");
        logger.info("time: {} from business calendar: {}", time, businessCalendar.getClass().getName());
    }
}
