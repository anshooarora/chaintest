package com.aventstack.chaintest.api.test;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateClientIdException extends RuntimeException {

    public DuplicateClientIdException(final String s) {
        super(s);
    }

}
