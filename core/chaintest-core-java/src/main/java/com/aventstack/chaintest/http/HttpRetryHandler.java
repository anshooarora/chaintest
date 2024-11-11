package com.aventstack.chaintest.http;

import com.aventstack.chaintest.domain.ChainTestEntity;
import com.aventstack.chaintest.domain.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.aventstack.chaintest.http.ChainTestApiClient.CLIENT_MAX_RETRIES;
import static com.aventstack.chaintest.http.ChainTestApiClient.CLIENT_RETRY_INTERVAL;
import static com.aventstack.chaintest.http.ChainTestApiClient.CLIENT_THROW_AFTER_RETRY_ATTEMPTS_EXCEEDED;

public class HttpRetryHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpRetryHandler.class);

    public static final int MAX_RETRY_ATTEMPTS = 3;
    public static final long RETRY_INTERVAL = 2000L;

    private final ChainTestApiClient _client;
    private final int _maxRetryAttempts;
    private final long _retryIntervalMs;
    private final boolean _throwAfterMaxRetryAttempts;

    public HttpRetryHandler(final ChainTestApiClient client, final Map<String, String> config) {
        this._client = client;

        final String maxRetries = config.get(CLIENT_MAX_RETRIES);
        _maxRetryAttempts = null != maxRetries && maxRetries.matches("\\d+")
                ? Integer.parseInt(maxRetries) : MAX_RETRY_ATTEMPTS;
        log.debug("Creating HttpRetryHandler instance for {} retry attempts", _maxRetryAttempts);

        final String retryInterval = config.get(CLIENT_RETRY_INTERVAL);
        _retryIntervalMs = null != retryInterval && retryInterval.matches("\\d+")
                ? Integer.parseInt(retryInterval) : RETRY_INTERVAL;

        final String throwAfterMaxRetryAttempts = config.get(CLIENT_THROW_AFTER_RETRY_ATTEMPTS_EXCEEDED);
        _throwAfterMaxRetryAttempts = Boolean.parseBoolean(throwAfterMaxRetryAttempts);
    }

    public HttpRetryHandler(final ChainTestApiClient client, final int maxRetryAttempts,
                            final long retryIntervalMs, final boolean throwAfterMaxRetryAttempts) {
        _client = client;
        _maxRetryAttempts = maxRetryAttempts;
        _retryIntervalMs = retryIntervalMs;
        _throwAfterMaxRetryAttempts = throwAfterMaxRetryAttempts;
    }

    public <T extends ChainTestEntity> HttpResponse<T> trySend(final T entity, final Class<T> clazz, final HttpMethod httpMethod,
                                                       final int maxRetryAttempts) throws IOException, InterruptedException {
        for (int i = 0; i <= maxRetryAttempts; i++) {
            if (i > 0) {
                log.trace("Retry: {}", i);
            }
            try {
                final HttpResponse<T> response = _client.send(entity, clazz, httpMethod);
                log.debug("Create API returned responseCode: {}", response.statusCode());
                if (200 == response.statusCode()) {
                    return response;
                } else if (400 <= response.statusCode() && 499 >= response.statusCode()) {
                    log.error("Failed to save entity {} due to a client-side error, received response code : {}",
                            clazz.getSimpleName(), response.statusCode());
                    return response;
                }
            } catch (final IOException | InterruptedException e) {
                if (e instanceof ConnectException) {
                    log.debug("Failed to connect to the ChainTest service", e);
                } else if (e instanceof HttpTimeoutException) {
                    log.debug("Timed out while waiting for ChainTest service response", e);
                } else {
                    log.debug("An exception occurred while sending entity", e);
                }
                if (i == maxRetryAttempts) {
                    if (_throwAfterMaxRetryAttempts) {
                        throw e;
                    }
                    break;
                }
            }
            Thread.sleep(_retryIntervalMs);
        }
        return null;
    }

    public <T extends ChainTestEntity> HttpResponse<T> trySend(final T entity, final Class<T> clazz, final HttpMethod httpMethod)
            throws IOException, InterruptedException {
        return trySend(entity, clazz, httpMethod, _maxRetryAttempts);
    }

    public synchronized Map<String, WrappedResponseAsync<Test>> sendWithRetries(final Map<String, WrappedResponseAsync<Test>> collection) {
        if (collection.isEmpty()) {
            return collection;
        }
        log.debug("Received collection of size {}. Handler will {}", collection.size(), (_maxRetryAttempts > 0)
                ? "retry for " + _maxRetryAttempts + " attempts on errors"
                : "will not retry on errors");
        final int size = collection.size();
        int retryAttempts = 0;
        final Map<String, WrappedResponseAsync<Test>> failures = new ConcurrentHashMap<>(collection);
        while (!failures.isEmpty() && retryAttempts++ <= _maxRetryAttempts) {
            trySendAsyncCollection(failures);
            if (!failures.isEmpty() && retryAttempts <= _maxRetryAttempts) {
                try {
                    Thread.sleep(_retryIntervalMs);
                    log.debug("Retrying {} of {} times", retryAttempts, _maxRetryAttempts);
                } catch (final InterruptedException ignored) {}
            }
        }
        if (!failures.isEmpty()) {
            final String message = String.format("Failed to transfer %d of %d tests. Make sure " +
                    "the ChainTest API is UP, ensure client-side logging is enabled or investigate API " +
            "logs to find the underlying cause.", failures.size(), size);
            log.error(message);
            if (_throwAfterMaxRetryAttempts) {
                throw new IllegalStateException(message);
            }
        }
        return failures;
    }

    private void trySendAsyncCollection(final Map<String, WrappedResponseAsync<Test>> collection) {
        collection.forEach((k, v) -> v.setError(null));
        for (final Map.Entry<String, WrappedResponseAsync<Test>> entry : collection.entrySet()) {
            final boolean completed = entry.getValue().getResponseFuture().isDone();
            if (!completed) {
                try {
                    entry.getValue().getResponseFuture().join();
                } catch (final Exception ignored) { }
            }
            final HttpResponse<Test> response = entry.getValue().getResponse();
            if (null != response) {
                if (200 == response.statusCode()) {
                    collection.entrySet().removeIf(x -> x.getKey().equals(entry.getKey()));
                    continue;
                } if (400 <= response.statusCode() && 499 >= response.statusCode()) {
                    log.error("Failed to persist entity {} due to a client-side error, received response code : {}",
                            Test.class.getSimpleName(), response.statusCode());
                    return;
                }
            }
        }
        for (final Map.Entry<String, WrappedResponseAsync<Test>> entry : collection.entrySet()) {
            try {
                entry.getValue().setResponseFuture(_client.sendAsync(entry.getValue().getEntity(), Test.class));
            } catch (final IOException ignore) { }
        }
    }

}
