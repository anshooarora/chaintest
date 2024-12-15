package com.aventstack.chainlp.util;

public class Assert {

    public static void notNull(final Object o, final String message) {
        if (o == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(final String s, final String message) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNullOrEmpty(final String s, final String message) {
        notNull(s, message);
        notEmpty(s, message);
    }

    public static void notEqual(final long lhs, final long rhs, final String message) {
        if (lhs == rhs) {
            throw new IllegalArgumentException(message);
        }
    }

}
