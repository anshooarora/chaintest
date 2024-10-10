package com.aventstack.chaintest.api.test;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingBuildPropertyException extends RuntimeException {

    public MissingBuildPropertyException(final String s) {
        super(s);
    }

}
