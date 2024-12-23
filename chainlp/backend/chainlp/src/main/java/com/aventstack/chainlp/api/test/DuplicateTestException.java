package com.aventstack.chainlp.api.test;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateTestException extends RuntimeException {

    public DuplicateTestException(final String message) {
        super(message);
    }

}
