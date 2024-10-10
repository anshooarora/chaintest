package com.aventstack.chaintest.api.build;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BuildNotFoundException extends RuntimeException {

    public BuildNotFoundException(final String s) {
        super(s);
    }

}
