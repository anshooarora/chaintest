package com.aventstack.chaintest.api.tagstats;

import com.aventstack.chaintest.api.domain.exception.BaseNotFoundException;

public class TagStatsNotFoundException extends BaseNotFoundException {

    public TagStatsNotFoundException(final String s) {
        super(s);
    }

}
