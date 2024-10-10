package com.aventstack.chaintest.api.tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TagNotFoundException extends RuntimeException {

    public TagNotFoundException(final String s) {
        super(s);
    }

}
