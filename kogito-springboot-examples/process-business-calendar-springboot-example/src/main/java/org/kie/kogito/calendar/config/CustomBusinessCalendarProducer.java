package org.kie.kogito.calendar.config;

import org.kie.kogito.calendar.BusinessCalendar;
import org.kie.kogito.calendar.bill.OneHourDelayCalendar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CustomBusinessCalendarProducer {

    @Bean
    @Primary
    public BusinessCalendar createBusinessCalendar() {
        return new OneHourDelayCalendar();
    }
}
