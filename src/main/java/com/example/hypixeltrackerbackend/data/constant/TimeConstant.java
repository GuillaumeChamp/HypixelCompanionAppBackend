package com.example.hypixeltrackerbackend.data.constant;

/**
 * Hold all constants relative to sampling and compressing frequency
 */
public class TimeConstant {
    public static final String DAY_TIME_WINDOW = "day";
    public static final String WEEK_TIME_WINDOW = "week";
    public static final String MONTH_TIME_WINDOW = "month";
    public static final String YEAR_TIME_WINDOW = "year";
    /**
     * Data are fetch every 30 seconds
     */
    public static final Integer CALL_FREQUENCY_IN_SECOND = 30;
    /**
     * After one hour, data are compress to keep only 10 records per hour which mean one every 6 minutes
     */
    public static final Integer RECORDS_PER_HOUR = 10;
    public static final Integer SAMPLING_BY_HOUR_TIME_SLOT_IN_MINUTES = 60 / RECORDS_PER_HOUR;
    /**
     * After one day, data are compress to keep only 72 records per day which mean one every 20 minutes i.e. every skyblock's day
     */
    public static final Integer RECORDS_PER_DAY = 72;
    public static final Integer SAMPLING_BY_DAY_TIME_SLOT_IN_MINUTES = 24*60/ RECORDS_PER_DAY;

    /**
     * After one week, data are compress to keep only 168 records per week which mean one every 60 minutes i.e. every 3 skyblock's day
     */
    public static final Integer RECORDS_PER_WEEK = 168;
    public static final Integer SAMPLING_BY_WEEK_TIME_SLOT_IN_MINUTES = 7*24*60/ RECORDS_PER_WEEK;

    private TimeConstant() {
    }
}
