package com.aventstack.chaintest.plugins;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Result;
import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.http.ChainTestApiClient;
import com.aventstack.chaintest.http.HttpMethod;
import com.aventstack.chaintest.http.HttpRetryHandler;
import com.aventstack.chaintest.http.WrappedResponseAsync;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.net.http.HttpResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestExecutionCallback
        implements BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final AtomicBoolean CALLBACK_INVOKED = new AtomicBoolean();
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final ConcurrentHashMap<String, WrappedResponseAsync<Test>> _tests = new ConcurrentHashMap<>();
    private static final String PROPERTY_PLUGINS_MAX_RETRIES = "chaintest.plugins.max-retries";

    private static ChainTestApiClient _client;
    private static Build _build = null;
    private static int _maxRetries = DEFAULT_MAX_RETRIES;

    @Override
    public void beforeAll(final ExtensionContext extensionContext) throws Exception {
        if (CALLBACK_INVOKED.getAndSet(true)) {
            return;
        }
        _client = new ChainTestApiClient();
        loadConfig();

        final Build build = new Build();
        final HttpResponse<Build> response = _client.send(build, Build.class);
        if (200 == response.statusCode()) {
            _build = response.body();
        }
    }

    private void loadConfig() {
        final String maxRetries = _client.getConfig().get(PROPERTY_PLUGINS_MAX_RETRIES);
        if (null != maxRetries && !maxRetries.isBlank() && maxRetries.chars().allMatch(Character::isDigit)) {
            _maxRetries = Integer.parseInt(maxRetries);
        }
    }

    @Override
    public void beforeTestExecution(final ExtensionContext context) {
        if (null == _build) {
            return;
        }
        final Test test = new Test(_build.getId(),
                context.getDisplayName(),
                context.getTestClass(),
                context.getTags());
        _build.addTags(context.getTags());
        _tests.put(context.getUniqueId(), new WrappedResponseAsync<>(test));
    }

    @Override
    public void afterTestExecution(final ExtensionContext context) throws Exception {
        if (null == _build) {
            return;
        }
        final WrappedResponseAsync<Test> test = _tests.get(context.getUniqueId());
        test.getEntity().complete(context.getExecutionException());
        test.setResponse(_client.sendAsync(test.getEntity(), Test.class));
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        if (null == _build) {
            return;
        }
        final boolean hasTestFailures = _tests.values().stream()
                .map(WrappedResponseAsync::getEntity)
                .anyMatch(x -> x.getResult().equals(Result.FAILED.getResult()));

        final HttpRetryHandler retryHandler = new HttpRetryHandler(_client, _maxRetries);
        retryHandler.retryWithAsync(_tests);

        if (!_tests.isEmpty()) {
            // handle
        }

        final Result buildResult = hasTestFailures ? Result.FAILED : Result.PASSED;
        _build.complete(buildResult);
        final HttpResponse<Build> response = _client.send(_build, Build.class, HttpMethod.PUT);
        if (200 == response.statusCode()) {
            _build = response.body();
        } else {
            // handle
        }
    }

}
