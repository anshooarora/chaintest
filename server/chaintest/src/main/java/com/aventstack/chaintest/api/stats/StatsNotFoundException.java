package com.aventstack.chaintest.api.stats;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StatsNotFoundException extends RuntimeException {

    public StatsNotFoundException(final String s) {
        super(s);
    }

}
