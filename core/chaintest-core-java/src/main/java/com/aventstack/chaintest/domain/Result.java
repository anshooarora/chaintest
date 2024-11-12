package com.aventstack.chaintest.domain;

public enum Result {

    PASSED("PASSED", 0),
    UNDEFINED("UNDEFINED", 10),
    SKIPPED("SKIPPED", 20),
    FAILED("FAILED", 30), ;

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

    public static Result computePriority(final Result a, final Result b) {
        return a.getPriority() > b.getPriority() ? a : b;
    }

}
