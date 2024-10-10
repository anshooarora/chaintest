package com.aventstack.chaintest.http;

import com.aventstack.chaintest.domain.ChainTestEntity;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class WrappedResponseAsync<T extends ChainTestEntity> extends BaseResponse<T> {

    private CompletableFuture<HttpResponse<T>> response;

    public WrappedResponseAsync(final T entity, final CompletableFuture<HttpResponse<T>> response) {
        super(entity);
        this.response = response;
    }

    public WrappedResponseAsync(final T entity) {
        super(entity);
    }

    public WrappedResponseAsync(final ErrorResponse error) {
        super(error);
    }

    public CompletableFuture<HttpResponse<T>> getResponse() {
        return response;
    }

    public void setResponse(CompletableFuture<HttpResponse<T>> response) {
        this.response = response;
    }

}
