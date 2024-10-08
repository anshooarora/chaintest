package com.aventstack.chaintest.http;

public enum HttpMethod {

    DELETE("DELETE"),
    GET("GET"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    POST("POST"),
    PUT("PUT");

    private final String _method;

    HttpMethod(final String method) {
        _method = method;
    }

    public String getMethod() {
        return _method;
    }

}
