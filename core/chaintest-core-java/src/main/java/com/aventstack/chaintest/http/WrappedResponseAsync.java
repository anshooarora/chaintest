package com.aventstack.chaintest.http;

import com.aventstack.chaintest.domain.ChainTestEntity;
import lombok.Getter;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;


@Getter
public class WrappedResponseAsync<T extends ChainTestEntity> extends WrappedResponse<T> {

    private CompletableFuture<HttpResponse<T>> responseFuture;

    public WrappedResponseAsync(final T entity, final CompletableFuture<HttpResponse<T>> response) {
        super(entity);
        this.responseFuture = response;
    }

    public WrappedResponseAsync(final T entity) {
        super(entity);
    }

    public WrappedResponseAsync(final ErrorResponse error) {
        super(error);
    }

    public void setResponseFuture(CompletableFuture<HttpResponse<T>> responseFuture) {
        responseFuture.whenComplete((res, e) -> {
            if (null != e) {
                setError(new ErrorResponse(e));
            } else {
                setResponse(res);
            }
        });
        this.responseFuture = responseFuture;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        WrappedResponseAsync<?> that = (WrappedResponseAsync<?>) obj;
        return responseFuture.equals(that.responseFuture);
    }

    @Override
    public int hashCode() {
        return responseFuture.hashCode();
    }

}
