package org.kie.kogito.calendar.bill;

import java.util.*;

import org.jbpm.process.core.timer.BusinessCalendarImpl;
import org.kie.kogito.calendar.BusinessCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom implementation of BusinessCalendar interface that is configured with properties.
 * BusinessCalendarImpl is used to calculate business time.
 */
public class OneHourDelayCalendar implements BusinessCalendar {

    private static final Logger logger = LoggerFactory.getLogger(OneHourDelayCalendar.class);
    private final BusinessCalendar businessCalendar;

    public OneHourDelayCalendar() {
        logger.debug("Instantiating OneHourDelayCalendar");
        this.businessCalendar = BusinessCalendarImpl.builder().build();
    }

    /**
     * @inheritDoc
     */
    @Override
    public long calculateBusinessTimeAsDuration(String timeExpression) {
        long time = calculateBusinessTimeAsDate(timeExpression).getTime();
        long timeDuration = time - System.currentTimeMillis();
        logger.debug("calculated time: {}, calculated time duration: {}", time, timeDuration);
        return timeDuration;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Date calculateBusinessTimeAsDate(String timeExpression) {
        Date date = this.businessCalendar.calculateBusinessTimeAsDate(timeExpression);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 1);
        Date dateAfterOneHour = calendar.getTime();
        logger.debug("Date after one hour: {}", dateAfterOneHour);
        return dateAfterOneHour;
    }

}
