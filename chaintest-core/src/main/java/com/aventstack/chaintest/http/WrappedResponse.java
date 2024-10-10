package com.aventstack.chaintest.http;

import com.aventstack.chaintest.domain.ChainTestEntity;

import java.net.http.HttpResponse;

public class WrappedResponse<T extends ChainTestEntity> extends BaseResponse<T> {

    private HttpResponse<T> response;

    public WrappedResponse(final T entity, final HttpResponse<T> response) {
        super(entity);
        this.response = response;
    }

    public WrappedResponse(final T entity) {
        super(entity);
    }

    public WrappedResponse(final ErrorResponse error) {
        super(error);
    }

    public HttpResponse<T> getResponse() {
        return response;
    }

    public void setResponse(HttpResponse<T> response) {
        this.response = response;
    }

}
