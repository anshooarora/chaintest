package com.aventstack.chaintest.api.runstats;

import com.aventstack.chaintest.api.domain.exception.BaseNotFoundException;

public class RunStatsNotFoundException extends BaseNotFoundException {

    public RunStatsNotFoundException(final String s) {
        super(s);
    }

}
