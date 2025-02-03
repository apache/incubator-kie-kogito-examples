package org.kie.kogito.calendar.bill;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;

import org.jbpm.process.core.timer.CalendarBean;
import org.jbpm.process.core.timer.CalendarBeanFactory;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.jbpm.util.PatternConstants;
import org.kie.kogito.calendar.BusinessCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Custom implementation of BusinessCalendar interface that is configured with properties.
 * Following are supported properties:
 * <ul>
 * <li>business.start.hour - specifies starting hour of work day (mandatory)</li>
 * <li>business.end.hour - specifies ending hour of work day (mandatory)</li>
 * <li>business.holidays - specifies holidays (see format section for details on how to configure it)</li>
 * <li>business.holiday.date.format - specifies holiday date format used (default yyyy-MM-dd)</li>
 * <li>business.weekend.days - specifies days of the weekend (default Saturday (7) and Sunday (1), use 0 to indicate no weekend days)</li>
 * <li>business.cal.timezone - specifies time zone to be used (if not given uses default of the system it runs on)</li>
 * </ul>
 *
 * <b>Format</b><br/>
 *
 * Holidays can be given in two formats:
 * <ul>
 * <li>as date range separated with colon - for instance 2012-05-01:2012-05-15</li>
 * <li>single day holiday - for instance 2012-05-01</li>
 * </ul>
 * each holiday period should be separated from next one with comma: 2012-05-01:2012-05-15,2012-12-24:2012-12-27
 * <br/>
 * Holiday date format must be given in pattern that is supported by <code>java.text.SimpleDateFormat</code>.<br/>
 *
 * Weekend days should be given as integer that corresponds to <code>java.util.Calendar</code> constants, use 0 to indicate no weekend days
 * <br/>
 */
@ApplicationScoped
public class NoHolidayOneHourDelayCalendar implements BusinessCalendar {

    private static final Logger logger = LoggerFactory.getLogger(NoHolidayOneHourDelayCalendar.class);

    private static final long HOUR_IN_MILLIS = 60 * 60 * 1000;

    private final int daysPerWeek;
    private final int hoursInDay;
    private final int startHour;
    private final int endHour;
    private final String timezone;

    private final List<TimePeriod> holidays;
    private final List<Integer> weekendDays;

    private static final int SIM_WEEK = 3;
    private static final int SIM_DAY = 5;
    private static final int SIM_HOU = 7;
    private static final int SIM_MIN = 9;
    private static final int SIM_SEC = 11;

    public static final String START_HOUR = "business.start.hour";
    public static final String END_HOUR = "business.end.hour";

    public NoHolidayOneHourDelayCalendar() {
        CalendarBean calendarBean = CalendarBeanFactory.createCalendarBean();
        //This calendar has no holidays
        holidays = Collections.emptyList();
        weekendDays = calendarBean.getWeekendDays();
        daysPerWeek = calendarBean.getDaysPerWeek();
        timezone = calendarBean.getTimezone();
        startHour = calendarBean.getStartHour();
        endHour = calendarBean.getEndHour();
        hoursInDay = calendarBean.getHoursInDay();
        logger.debug("\tholidays: {},\n\tweekendDays: {},\n\tdaysPerWeek: {},\n\ttimezone: {},\n\tstartHour: {},\n\tendHour: {},\n\thoursInDay: {}",
                holidays, weekendDays, daysPerWeek, timezone, startHour, endHour, hoursInDay);
    }

    /**
     * @inheritDoc
     */
    @Override
    public long calculateBusinessTimeAsDuration(String timeExpression) {
        logger.trace("timeExpression {}", timeExpression);
        timeExpression = adoptISOFormat(timeExpression);

        Date calculatedDate = calculateBusinessTimeAsDate(timeExpression);
        logger.debug("calculatedDate: {}, currentTime: {}, timeExpression: {}, Difference: {} ms",
                calculatedDate, new Date(getCurrentTime()), timeExpression, calculatedDate.getTime() - getCurrentTime());

        return (calculatedDate.getTime() - getCurrentTime());
    }

    /**
     * @inheritDoc
     */
    @Override
    public Date calculateBusinessTimeAsDate(String timeExpression) {
        logger.trace("timeExpression {}", timeExpression);
        timeExpression = adoptISOFormat(timeExpression);

        String trimmed = timeExpression.trim();
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int min = 0;
        int sec = 0;

        if (!trimmed.isEmpty()) {
            Matcher mat = PatternConstants.SIMPLE_TIME_DATE_MATCHER.matcher(trimmed);
            if (mat.matches()) {
                weeks = (mat.group(SIM_WEEK) != null) ? Integer.parseInt(mat.group(SIM_WEEK)) : 0;
                days = (mat.group(SIM_DAY) != null) ? Integer.parseInt(mat.group(SIM_DAY)) : 0;
                hours = (mat.group(SIM_HOU) != null) ? Integer.parseInt(mat.group(SIM_HOU)) : 0;
                min = (mat.group(SIM_MIN) != null) ? Integer.parseInt(mat.group(SIM_MIN)) : 0;
                sec = (mat.group(SIM_SEC) != null) ? Integer.parseInt(mat.group(SIM_SEC)) : 0;
            }
        }
        logger.trace("weeks: {}", weeks);
        logger.trace("days: {}", days);
        logger.trace("hours: {}", hours);
        logger.trace("min: {}", min);
        logger.trace("sec: {}", sec);
        int time = 0;

        Calendar calendar = getCalendar();
        logger.trace("calendar selected for business calendar: {}", calendar.getTime());
        if (timezone != null) {
            calendar.setTimeZone(TimeZone.getTimeZone(timezone));
        }

        // calculate number of weeks
        int numberOfWeeks = days / daysPerWeek + weeks;
        logger.trace("number of weeks: {}", numberOfWeeks);
        if (numberOfWeeks > 0) {
            calendar.add(Calendar.WEEK_OF_YEAR, numberOfWeeks);
        }
        logger.trace("calendar WEEK_OF_YEAR: {}", calendar.get(Calendar.WEEK_OF_YEAR));
        rollCalendarToNextWorkingDayIfCurrentDayIsNonWorking(calendar, weekendDays, hours > 0 || min > 0);
        hours += (days - (numberOfWeeks * daysPerWeek)) * hoursInDay;

        // calculate number of days
        int numberOfDays = hours / hoursInDay;
        logger.trace("numberOfDays: {}", numberOfDays);
        if (numberOfDays > 0) {
            for (int i = 0; i < numberOfDays; i++) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                boolean resetTime = false;
                rollCalendarToNextWorkingDayIfCurrentDayIsNonWorking(calendar, weekendDays, resetTime);
                logger.trace("calendar after rolling to next working day: {} when number of days > 0", calendar.getTime());
                rollCalendarAfterHolidays(calendar, holidays, weekendDays, hours > 0 || min > 0);
                logger.trace("calendar after holidays when number of days > 0: {}", calendar.getTime());
            }
        }
        int currentCalHour = calendar.get(Calendar.HOUR_OF_DAY);
        boolean resetMinuteSecond = currentCalHour >= endHour || currentCalHour < startHour;
        rollCalendarToWorkingHour(calendar, resetMinuteSecond);
        logger.trace("calendar after rolling to working hour: {}", calendar.getTime());

        // calculate remaining hours
        time = hours - (numberOfDays * hoursInDay);
        calendar.add(Calendar.HOUR, time);
        logger.trace("calendar after adding time {}: {}", time, calendar.getTime());
        boolean resetTime = true;
        rollCalendarToNextWorkingDayIfCurrentDayIsNonWorking(calendar, weekendDays, resetTime);
        logger.trace("calendar after rolling to next working day: {}", calendar.getTime());
        rollCalendarAfterHolidays(calendar, holidays, weekendDays, hours > 0 || min > 0);
        logger.trace("calendar after holidays: {}", calendar.getTime());
        rollCalendarToWorkingHour(calendar, false);
        logger.trace("calendar after rolling to working hour: {}", calendar.getTime());

        // calculate minutes
        int numberOfHours = min / 60;
        if (numberOfHours > 0) {
            calendar.add(Calendar.HOUR, numberOfHours);
            min = min - (numberOfHours * 60);
        }
        calendar.add(Calendar.MINUTE, min);

        // calculate seconds
        int numberOfMinutes = sec / 60;
        if (numberOfMinutes > 0) {
            calendar.add(Calendar.MINUTE, numberOfMinutes);
            sec = sec - (numberOfMinutes * 60);
        }
        calendar.add(Calendar.SECOND, sec);
        logger.trace("calendar after adding {} hour, {} minutes and {} seconds: {}", numberOfHours, numberOfMinutes, sec, calendar.getTime());

        rollCalendarToWorkingHour(calendar, false);
        logger.trace("calendar after rolling to next working day: {}", calendar.getTime());

        // take under consideration weekend
        resetTime = false;
        rollCalendarToNextWorkingDayIfCurrentDayIsNonWorking(calendar, weekendDays, resetTime);
        logger.trace("calendar after rolling to next working day: {}", calendar.getTime());
        // take under consideration holidays
        rollCalendarAfterHolidays(calendar, holidays, weekendDays, resetTime);
        logger.trace("calendar after holidays: {}", calendar.getTime());

        //Adding 1 hour to the time
        calendar.add(Calendar.HOUR, 1);
        return calendar.getTime();
    }

    /**
     * Indirection used only for testing purposes
     *
     * @return
     */
    protected Calendar getCalendar() {
        String debugMessage = "Return new GregorianCalendar";
        logger.trace(debugMessage);
        return new GregorianCalendar();
    }

    /**
     * Rolls the <code>HOUR_OF_DAY</code> of the given <code>Calendar</code> depending on
     * given <code>currentCalHour</code>, instance <code>endHour</code>, and instance <code>startHour</code>
     *
     * It also consider if the startHour < endHour (i.e. working daily hours) or startHour > endHour (i.e. nightly daily hours).
     *
     * The case where startHour = endHour is excluded by validation of the <code>CalendarBean</code>
     *
     * @param toRoll
     * @param resetMinuteSecond if <code>true</code>, set minutes and seconds to 0
     */
    protected void rollCalendarToWorkingHour(Calendar toRoll, boolean resetMinuteSecond) {
        logger.trace("toRoll: {}", toRoll.getTime());
        if (startHour < endHour) {
            rollCalendarToDailyWorkingHour(toRoll, startHour, endHour);
        } else {
            throw new UnsupportedOperationException(String.format("This feature is not supported yet: %s should be greater than %s", END_HOUR, START_HOUR));
        }
        if (resetMinuteSecond) {
            toRoll.set(Calendar.MINUTE, 0);
            toRoll.set(Calendar.SECOND, 0);
        }
    }

    /**
     * Rolls the <code>HOUR_OF_DAY</code> of the given <code>Calendar</code> to the next "daily" working hour
     *
     * @param toRoll
     * @param startHour
     * @param endHour
     */
    static void rollCalendarToDailyWorkingHour(Calendar toRoll, int startHour, int endHour) {
        logger.trace("toRoll: {}", toRoll.getTime());
        logger.trace("startHour: {}", startHour);
        logger.trace("endHour: {}", endHour);
        int currentCalHour = toRoll.get(Calendar.HOUR_OF_DAY);
        if (currentCalHour >= endHour) {
            toRoll.add(Calendar.DAY_OF_YEAR, 1);
            // set hour to the starting one
            toRoll.set(Calendar.HOUR_OF_DAY, startHour);
        } else if (currentCalHour < startHour) {
            toRoll.add(Calendar.HOUR_OF_DAY, startHour - currentCalHour);
        }
        logger.trace("calendar after rolling to daily working hour: {}", toRoll.getTime());
    }

    /**
     * Rolls the <code>HOUR_OF_DAY</code> of the given <code>Calendar</code> to the next "nightly" working hour
     *
     * @param toRoll
     * @param startHour
     * @param endHour
     */
    static void rollCalendarToNightlyWorkingHour(Calendar toRoll, int startHour, int endHour) {
        logger.trace("toRoll: {}", toRoll.getTime());
        logger.trace("startHour: {}", startHour);
        logger.trace("endHour: {}", endHour);
        int currentCalHour = toRoll.get(Calendar.HOUR_OF_DAY);
        if (currentCalHour < endHour) {
            toRoll.set(Calendar.HOUR_OF_DAY, endHour);
        } else if (currentCalHour >= startHour) {
            toRoll.add(Calendar.DAY_OF_YEAR, 1);
            toRoll.set(Calendar.HOUR_OF_DAY, endHour);
        }
        toRoll.set(Calendar.MINUTE, 0);
        toRoll.set(Calendar.SECOND, 0);
        logger.debug("calendar after rolling to nightly working hour: {}", toRoll.getTime());
    }

    /**
     * Rolls the given <code>Calendar</code> to the first <b>working day</b>
     * after configured <code>holidays</code>, if provided.
     *
     * Set hour, minute, second and millisecond when
     * <code>resetTime</code> is <code>true</code>
     *
     * @param toRoll
     * @param holidays
     * @param resetTime
     */
    static void rollCalendarAfterHolidays(Calendar toRoll, List<TimePeriod> holidays, List<Integer> weekendDays, boolean resetTime) {
        logger.trace("toRoll: {}", toRoll.getTime());
        logger.trace("holidays: {}", holidays);
        logger.trace("weekendDays: {}", weekendDays);
        logger.trace("resetTime: {}", resetTime);
        if (!holidays.isEmpty()) {
            Date current = toRoll.getTime();
            for (TimePeriod holiday : holidays) {
                // check each holiday if it overlaps current date and break after first match
                if (current.after(holiday.getFrom()) && current.before(holiday.getTo())) {

                    Calendar lastHolidayDayTime = new GregorianCalendar();
                    lastHolidayDayTime.setTime(holiday.getTo());

                    Calendar currentDayTmp = new GregorianCalendar();
                    currentDayTmp.setTime(current);
                    currentDayTmp.set(Calendar.HOUR_OF_DAY, 0);
                    currentDayTmp.set(Calendar.MINUTE, 0);
                    currentDayTmp.set(Calendar.SECOND, 0);
                    currentDayTmp.set(Calendar.MILLISECOND, 0);

                    long difference = lastHolidayDayTime.getTimeInMillis() - currentDayTmp.getTimeInMillis();
                    int dayDifference = (int) Math.ceil(difference / (HOUR_IN_MILLIS * 24d));

                    toRoll.add(Calendar.DAY_OF_MONTH, dayDifference);

                    rollCalendarToNextWorkingDayIfCurrentDayIsNonWorking(toRoll, weekendDays, resetTime);
                    break;
                }
            }
        }

    }

    /**
     * Rolls the given <code>Calendar</code> to the first <b>working day</b>
     * Set hour, minute, second and millisecond when
     * <code>resetTime</code> is <code>true</code>
     *
     * @param toRoll
     * @param resetTime
     */
    static void rollCalendarToNextWorkingDayIfCurrentDayIsNonWorking(Calendar toRoll, List<Integer> weekendDays, boolean resetTime) {
        logger.trace("toRoll: {}", toRoll.getTime());
        logger.trace("weekendDays: {}", weekendDays);
        logger.trace("resetTime: {}", resetTime);
        int dayOfTheWeek = toRoll.get(Calendar.DAY_OF_WEEK);
        logger.trace("dayOfTheWeek: {}", dayOfTheWeek);
        while (!isWorkingDay(weekendDays, dayOfTheWeek)) {
            toRoll.add(Calendar.DAY_OF_YEAR, 1);
            if (resetTime) {
                toRoll.set(Calendar.HOUR_OF_DAY, 0);
                toRoll.set(Calendar.MINUTE, 0);
                toRoll.set(Calendar.SECOND, 0);
                toRoll.set(Calendar.MILLISECOND, 0);
            }
            dayOfTheWeek = toRoll.get(Calendar.DAY_OF_WEEK);
        }
        logger.trace("dayOfTheWeek after rolling calendar: {}", dayOfTheWeek);
    }

    static boolean isWorkingDay(List<Integer> weekendDays, int day) {
        logger.trace("weekendDays: {}", weekendDays);
        logger.trace("day: {}", day);
        return !weekendDays.contains(day);
    }

    protected long getCurrentTime() {
        return System.currentTimeMillis();
    }

    protected String adoptISOFormat(String timeExpression) {
        logger.trace("timeExpression: {}", timeExpression);
        try {
            Duration p = null;
            if (DateTimeUtils.isPeriod(timeExpression)) {
                p = Duration.parse(timeExpression);
            } else if (DateTimeUtils.isNumeric(timeExpression)) {
                p = Duration.of(Long.valueOf(timeExpression), ChronoUnit.MILLIS);
            } else {
                OffsetDateTime dateTime = OffsetDateTime.parse(timeExpression, DateTimeFormatter.ISO_DATE_TIME);
                p = Duration.between(OffsetDateTime.now(), dateTime);
            }

            long days = p.toDays();
            long hours = p.toHours() % 24;
            long minutes = p.toMinutes() % 60;
            long seconds = p.getSeconds() % 60;
            long milis = p.toMillis() % 1000;

            StringBuffer time = new StringBuffer();
            if (days > 0) {
                time.append(days + "d");
            }
            if (hours > 0) {
                time.append(hours + "h");
            }
            if (minutes > 0) {
                time.append(minutes + "m");
            }
            if (seconds > 0) {
                time.append(seconds + "s");
            }
            if (milis > 0) {
                time.append(milis + "ms");
            }

            return time.toString();
        } catch (Exception e) {
            return timeExpression;
        }
    }

    static class TimePeriod {
        private Date from;
        private Date to;

        protected TimePeriod(Date from, Date to) {
            this.from = from;
            this.to = to;
        }

        protected Date getFrom() {
            return this.from;
        }

        protected Date getTo() {
            return this.to;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TimePeriod that)) {
                return false;
            }
            return Objects.equals(from, that.from) && Objects.equals(to, that.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }

        @Override
        public String toString() {
            return "TimePeriod{" +
                    "from=" + from +
                    ", to=" + to +
                    '}';
        }
    }
}
