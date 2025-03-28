package com.aventstack.chaintest.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    public static String match(final String pattern, final String text, final int group) {
        final Matcher m = Pattern.compile(pattern).matcher(text);
        if (m.find()) {
            return m.group(group);
        }
        return null;
    }

    public static String match(final String pattern, final String text) {
        return match(pattern, text, 0);
    }

}
