package com.aventstack.chaintest.api.domain;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResponse {

    private String error;
    private HttpStatus status;
    private String stacktrace;

    public ErrorResponse(final String error, final HttpStatus status) {
        this.error = error;
        this.status = status;
    }

}
