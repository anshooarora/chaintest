package com.aventstack.chaintest.http;

import com.aventstack.chaintest.domain.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HttpRetryHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpRetryHandler.class);

    private final ChainTestApiClient _client;
    private final int _maxRetryAttempts;

    public HttpRetryHandler(final ChainTestApiClient client, final int maxRetryAttempts) {
        this._client = client;
        _maxRetryAttempts = maxRetryAttempts;
    }

    public void sendWithRetriesAsync(final ConcurrentHashMap<String, WrappedResponseAsync<Test>> collection) {
        final int size = collection.size();
        int retryAttempts = 0;
        while (!collection.isEmpty() && retryAttempts++ <= _maxRetryAttempts) {
            if (retryAttempts > 1) {
                log.debug("Retrying " + (retryAttempts - 1) + " of " + _maxRetryAttempts + " times");
            }
            sendAsync(collection);
        }
        if (!collection.isEmpty()) {
            log.error("Failed to transfer " + collection.size() + " of " + size + " tests. Make sure " +
                    "the ChainTest API is UP, ensure client-side logging is enabled or investigate API logs " +
                    "to find the underlying cause.");
        }
    }

    private void sendAsync(final ConcurrentHashMap<String, WrappedResponseAsync<Test>> collection) {
        final List<CompletableFuture<HttpResponse<Test>>> responses = collection.values().stream()
                .map(WrappedResponseAsync::getResponse)
                .collect(Collectors.toUnmodifiableList());
        try {
            CompletableFuture.allOf(responses.toArray(CompletableFuture[]::new)).join();
            collection.clear();
        } catch (final Exception ignored) {
            for (final CompletableFuture<HttpResponse<Test>> response : responses) {
                response.thenAccept(x -> collection.values().removeIf(r -> r.getResponse() == response))
                        .exceptionally(e -> {
                            log.debug("Failed to transfer test", e);
                            return null;
                        });
            }
            for (final Map.Entry<String, WrappedResponseAsync<Test>> entry : collection.entrySet()) {
                try {
                    entry.getValue().setResponse(_client.sendAsync(entry.getValue().getEntity(), Test.class));
                } catch (final IOException ignore) { }
            }
        }
    }

}