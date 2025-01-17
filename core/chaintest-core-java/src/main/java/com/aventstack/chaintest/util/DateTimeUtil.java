package com.aventstack.chaintest.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static boolean isPatternValid(final String pattern) {
        try {
            DateTimeFormatter.ofPattern(pattern);
            return true;
        } catch (final Exception ignored) {
            return false;
        }
    }

    public static String toFormat(final long millis, final String pattern) {
        return DateTimeFormatter.ofPattern(pattern)
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(millis));
    }

}
