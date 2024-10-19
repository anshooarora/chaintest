package com.aventstack.chaintest.api.runstats;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RunStatsNotFoundException extends RuntimeException {

    public RunStatsNotFoundException(final String s) {
        super(s);
    }

}
