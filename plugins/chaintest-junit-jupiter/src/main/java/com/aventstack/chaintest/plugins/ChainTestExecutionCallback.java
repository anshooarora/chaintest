package com.aventstack.chaintest.plugins;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Result;
import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.http.ChainTestApiClient;
import com.aventstack.chaintest.http.HttpMethod;
import com.aventstack.chaintest.http.WrappedResponseAsync;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChainTestExecutionCallback
        implements BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger log = LoggerFactory.getLogger(ChainTestExecutionCallback.class);
    private static final String JUNIT_JUPITER = "junit-jupiter";
    private static final AtomicBoolean CALLBACK_INVOKED = new AtomicBoolean();
    private static final ConcurrentHashMap<String, WrappedResponseAsync<Test>> _tests = new ConcurrentHashMap<>();

    private static ChainTestApiClient _client;
    private static Build _build = null;

    @Override
    public void beforeAll(final ExtensionContext extensionContext) throws Exception {
        if (CALLBACK_INVOKED.getAndSet(true)) {
            return;
        }
        log.trace("Creating instance of " + ChainTestApiClient.class);
        _client = new ChainTestApiClient();

        log.trace("Starting new build, but events will only be sent to API if build is successfully created");
        final Build build = new Build(JUNIT_JUPITER);
        final HttpResponse<Build> response = _client.send(build, Build.class);
        log.debug("Create build API returned responseCode: " + response.statusCode());
        if (200 == response.statusCode()) {
            _build = response.body();
            log.debug("All tests in this run will be associated with buildId: " + _build.getId());
        }
    }

    @Override
    public void beforeTestExecution(final ExtensionContext context) {
        if (null == _build) {
            return;
        }
        log.trace("Creating test " + context.getDisplayName());
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
        log.trace("Ended test " + test.getEntity().getName() + " with status " + test.getEntity().getResult());
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        if (null == _build) {
            return;
        }
        log.trace("Executing afterAll hook");

        final boolean hasTestFailures = _tests.values().stream()
                .map(WrappedResponseAsync::getEntity)
                .anyMatch(x -> x.getResult().equals(Result.FAILED.getResult()));

        _client.retryHandler().sendWithRetriesAsync(_tests);

        final Result buildResult = hasTestFailures ? Result.FAILED : Result.PASSED;
        _build.complete(buildResult);
        _client.send(_build, Build.class, HttpMethod.PUT);
    }

}
