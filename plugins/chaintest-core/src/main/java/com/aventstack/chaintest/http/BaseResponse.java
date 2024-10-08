package com.aventstack.chaintest.http;

import com.aventstack.chaintest.domain.ChainTestEntity;

public abstract class BaseResponse<T extends ChainTestEntity> {

    private T entity;
    private ErrorResponse error;

    public BaseResponse(final T entity) {
        this.entity = entity;
    }

    public BaseResponse(final ErrorResponse error) {
        this.error = error;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

}
