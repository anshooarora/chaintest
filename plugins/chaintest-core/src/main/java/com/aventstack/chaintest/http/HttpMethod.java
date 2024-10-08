package com.aventstack.chaintest.http;

public enum HttpMethod {

    DELETE("delete"),
    GET("get"),
    HEAD("head"),
    OPTIONS("options"),
    POST("post"),
    PUT("put");

    private final String _method;

    HttpMethod(final String method) {
        _method = method;
    }

    public String getMethod() {
        return _method;
    }

}
