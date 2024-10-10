package com.aventstack.chaintest.domain;

public enum Result {

    PASSED("PASSED"), FAILED("FAILED"), SKIPPED("SKIPPED");

    private final String _result;

    Result(final String result) {
        _result = result;
    }

    public String getResult() {
        return _result;
    }

}
