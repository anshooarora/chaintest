package com.aventstack.chaintest.api.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public abstract class BaseNotFoundException extends RuntimeException {

    public BaseNotFoundException(final String message) {
        super(message);
    }

}
