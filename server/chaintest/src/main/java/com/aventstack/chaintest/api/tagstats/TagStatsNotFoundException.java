package com.aventstack.chaintest.api.tagstats;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TagStatsNotFoundException extends RuntimeException {

    public TagStatsNotFoundException(final String s) {
        super(s);
    }

}
