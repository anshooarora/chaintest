package com.aventstack.chaintest.http;

import com.aventstack.chaintest.domain.ChainTestEntity;

import java.net.http.HttpResponse;

public class WrappedResponse<T extends ChainTestEntity> {

    private T entity;
    private HttpResponse<T> response;
    private ErrorResponse error;

    public WrappedResponse(final T entity, final HttpResponse<T> response) {
        this.entity = entity;
        this.response = response;
    }

    public WrappedResponse(final T entity) {
        this.entity = entity;
    }

    public WrappedResponse(final ErrorResponse error) {
        this.error = error;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public HttpResponse<T> getResponse() {
        return response;
    }

    public void setResponse(HttpResponse<T> response) {
        this.response = response;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

}
