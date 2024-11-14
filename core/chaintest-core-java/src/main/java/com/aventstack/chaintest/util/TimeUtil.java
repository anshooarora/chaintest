package com.aventstack.chaintest.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static String getPrettyTime(final long millis) {
        return String.format("%dh %dm %ds",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

}
