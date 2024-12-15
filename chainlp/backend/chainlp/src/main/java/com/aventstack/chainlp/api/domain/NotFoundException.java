package com.aventstack.chainlp.api.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public abstract class NotFoundException extends RuntimeException {

    public NotFoundException(final String message) {
        super(message);
    }

}