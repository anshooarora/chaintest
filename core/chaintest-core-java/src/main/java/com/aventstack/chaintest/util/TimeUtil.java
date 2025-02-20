package com.aventstack.chaintest.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static String getPrettyTime(final long millis) {
        if (1_000L > millis) {
            return millis + "ms";
        }
        if (60_000L > millis) {
            return String.format("%ds",
                    TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
        }
        if (3_600_000L > millis) {
            return String.format("%dm %ds",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        }
        return String.format("%dh %dm %ds",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % 60,
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }

}
