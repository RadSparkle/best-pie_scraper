package com.bestpie.scraper.common.utils;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;


@Component
public class TimeUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

    public static String getCurrentTime() {
        DateTime dateTime = new DateTime();
        return dateTime.toString(FORMATTER);
    }
}
