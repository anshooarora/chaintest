package com.aventstack.chaintest.http;

import com.aventstack.chaintest.domain.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HttpRetryHandler {

    private final ChainTestApiClient _client;
    private final int _maxRetryAttempts;

    public HttpRetryHandler(final ChainTestApiClient client, final int maxRetryAttempts) {
        this._client = client;
        _maxRetryAttempts = maxRetryAttempts;
    }

    public void retryWithAsync(final ConcurrentHashMap<String, WrappedResponseAsync<Test>> collection) {
        int retryAttempts = 0;
        while (!collection.isEmpty() && retryAttempts++ < _maxRetryAttempts) {
            retryAsync(collection);
        }
        if (!collection.isEmpty()) {
            // handle
        }
    }

    private void retryAsync(final ConcurrentHashMap<String, WrappedResponseAsync<Test>> collection) {
        final List<CompletableFuture<HttpResponse<Test>>> responses = collection.values().stream()
                .map(WrappedResponseAsync::getResponse)
                .collect(Collectors.toUnmodifiableList());
        try {
            CompletableFuture.allOf(responses.toArray(CompletableFuture[]::new)).join();
            collection.clear();
        } catch (final Exception ignored) {
            for (final CompletableFuture<HttpResponse<Test>> response : responses) {
                response.thenAccept(x -> collection.values().removeIf(r -> r.getResponse() == response));
            }
            for (final Map.Entry<String, WrappedResponseAsync<Test>> entry : collection.entrySet()) {
                try {
                    entry.getValue().setResponse(_client.sendAsync(entry.getValue().getEntity(), Test.class));
                } catch (final IOException ignore) { }
            }
        }
    }

}
