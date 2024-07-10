package com.example.hypixeltrackerbackend.constant;

public class TimeConstant {
    public static final Integer CALL_FREQUENCY_IN_SECOND = 30;
    public static final Integer VALUES_BY_HOURS = 20;
    public static final Integer SAMPLING_BY_HOURS_TIME_SLOT_IN_MINUTES = 60/VALUES_BY_HOURS;
    public static final Integer VALUES_BY_DAYS = 72;
    public static final Integer SAMPLING_BY_DAYS_TIME_SLOT_IN_MINUTES = 24*60/VALUES_BY_DAYS;
    private TimeConstant(){}
}
