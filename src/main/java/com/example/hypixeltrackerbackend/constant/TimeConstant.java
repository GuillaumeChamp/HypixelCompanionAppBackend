package com.example.hypixeltrackerbackend.constant;

/**
 * Hold all constant relative to sampling and compressing frequency
 */
public class TimeConstant {
    /**
     * Data are fetch every 30 seconds
     */
    public static final Integer CALL_FREQUENCY_IN_SECOND = 30;
    /**
     * After one hour, data are compress to keep only 10 data per hour which mean one every 6 minutes
     */
    public static final Integer VALUES_BY_HOURS = 10;
    public static final Integer SAMPLING_BY_HOURS_TIME_SLOT_IN_MINUTES = 60/VALUES_BY_HOURS;
    /**
     * After one day, data are compress to keep only 72 data per day which mean one every 20 minutes i.e. every skyblock day
     */
    public static final Integer VALUES_BY_DAYS = 72;
    public static final Integer SAMPLING_BY_DAYS_TIME_SLOT_IN_MINUTES = 24*60/VALUES_BY_DAYS;
    private TimeConstant(){}
}
