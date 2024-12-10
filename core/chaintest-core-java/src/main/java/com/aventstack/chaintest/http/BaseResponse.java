package com.aventstack.chaintest.http;

import com.aventstack.chaintest.domain.ChainTestEntity;
import lombok.Data;

@Data
public abstract class BaseResponse<T extends ChainTestEntity> {

    private T entity;
    private ErrorResponse error;

    public BaseResponse(final T entity) {
        this.entity = entity;
    }

    public BaseResponse(final ErrorResponse error) {
        this.error = error;
    }

}
