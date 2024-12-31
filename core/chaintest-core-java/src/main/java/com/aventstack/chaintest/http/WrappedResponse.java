package com.aventstack.chaintest.http;

import com.aventstack.chaintest.domain.ChainTestEntity;
import lombok.Getter;
import lombok.Setter;

import java.net.http.HttpResponse;

@Setter
@Getter
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        WrappedResponse<?> that = (WrappedResponse<?>) obj;
        return response.equals(that.response);
    }

    @Override
    public int hashCode() {
        return response.hashCode();
    }

}
