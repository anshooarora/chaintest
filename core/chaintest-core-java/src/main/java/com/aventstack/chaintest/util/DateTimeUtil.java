package com.aventstack.chaintest.util;

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

}
