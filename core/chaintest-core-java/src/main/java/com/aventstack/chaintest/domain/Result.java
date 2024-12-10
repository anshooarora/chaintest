package com.aventstack.chaintest.domain;

import java.util.Arrays;

public enum Result {

    UNKNOWN("UNKNOWN", -1),
    PASSED("PASSED", 0),
    UNDEFINED("UNDEFINED", 10),
    SKIPPED("SKIPPED", 20),
    FAILED("FAILED", 30);

    private final String _result;
    private final int _priority;

    Result(final String result, final int priority) {
        _result = result;
        _priority = priority;
    }

    public String getResult() {
        return _result;
    }

    public int getPriority() {
        return _priority;
    }

    public static Result parseResult(final String resultString) {
        return Arrays.stream(values())
                .filter(x -> x.getResult().equalsIgnoreCase(resultString))
                .findAny()
                .orElse(UNKNOWN);
    }

    public static Result computePriority(final Result a, final Result b) {
        return a.getPriority() > b.getPriority() ? a : b;
    }

    public static Result computePriority(final String a, final String b) {
        final Result ar = parseResult(a);
        final Result br = parseResult(b);
        return computePriority(ar, br);
    }

}
