package org.kie.kogito.calendar;

import org.junit.jupiter.api.Test;
import org.kie.kogito.calendar.bill.NoHolidayOneHourDelayCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
public class BusinessCalendarBeanTest {
    @Autowired
    private BusinessCalendar createBusinessCalendar;

    private final Logger logger = LoggerFactory.getLogger(BusinessCalendarBeanTest.class);

    @Test
    void testBusinessCalendar() {
        long time = createBusinessCalendar.calculateBusinessTimeAsDuration("test");
        logger.info("time: {} from business calendar: {}", time, createBusinessCalendar.getClass().getName());
        assertInstanceOf(NoHolidayOneHourDelayCalendar.class, createBusinessCalendar);
    }
}
