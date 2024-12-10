package com.aventstack.chaintest.http;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorResponse {

    private Throwable throwable;
    private Date timestamp;
    private int status;
    private String error;
    private String trace;
    private String message;
    private String path;

    public ErrorResponse(final Throwable t) {
        throwable = t;
    }

}
