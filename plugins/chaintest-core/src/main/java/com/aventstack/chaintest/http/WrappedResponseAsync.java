package com.aventstack.chaintest.http;

import com.aventstack.chaintest.domain.ChainTestEntity;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class WrappedResponseAsync<T extends ChainTestEntity> {

    private T entity;
    private CompletableFuture<HttpResponse<T>> response;
    private ErrorResponse error;


    public WrappedResponseAsync(final T entity, final CompletableFuture<HttpResponse<T>> response) {
        this.entity = entity;
        this.response = response;
    }

    public WrappedResponseAsync(final T entity) {
        this.entity = entity;
    }

    public WrappedResponseAsync(final ErrorResponse error) {
        this.error = error;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public CompletableFuture<HttpResponse<T>> getResponse() {
        return response;
    }

    public void setResponse(CompletableFuture<HttpResponse<T>> response) {
        this.response = response;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

}
