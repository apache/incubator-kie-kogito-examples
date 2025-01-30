package org.kie.kogito.calendar.bill;

import org.kie.kogito.calendar.BusinessCalendar;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
@Alternative
@Priority(1)
public class CustomBusinessCalendarProducer {

    @Produces
    public BusinessCalendar createBusinessCalendar() {
        return new CustomBusinessCalendar();
    }
}
