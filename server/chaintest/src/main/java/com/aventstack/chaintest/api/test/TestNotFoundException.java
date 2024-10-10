package com.aventstack.chaintest.api.test;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TestNotFoundException extends RuntimeException {

    public TestNotFoundException(final String s) {
        super(s);
    }

}
