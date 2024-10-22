package com.aventstack.chaintest.service;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Result;
import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.http.ChainTestApiClient;
import com.aventstack.chaintest.http.HttpMethod;
import com.aventstack.chaintest.http.WrappedResponseAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChainPluginService {

    private static final Logger log = LoggerFactory.getLogger(ChainPluginService.class);
    private static final ConcurrentHashMap<String, WrappedResponseAsync<Test>> _wrappedResponses = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, Test> _tests = new ConcurrentHashMap<>();
    private static final AtomicBoolean CALLBACK_INVOKED = new AtomicBoolean();

    private final ChainTestApiClient _client;
    private final String _testRunner;
    private final int _maxRetries;
    private Build _build;

    public ChainPluginService(final ChainTestApiClient client, final String testRunner) {
        _client = client;
        _maxRetries = Math.max(Integer.parseInt(_client.config().getConfig().get(ChainTestApiClient.PROPERTY_CLIENT_MAX_RETRIES)), 0);
        _testRunner = testRunner;
    }

    public ChainPluginService(final String testRunner) throws IOException {
        this(new ChainTestApiClient(), testRunner);
    }

    public ChainTestApiClient getClient() {
        return _client;
    }

    public Build getBuild() {
        return _build;
    }

    public boolean start() {
        log.trace("Starting new build, but events will only be sent to API if build is successfully created");
        _build = new Build(_testRunner);
        try {
            trySendBuild(HttpMethod.POST);
            return true;
        } catch (final IOException | InterruptedException e) {
            log.error("Failed to send Build. PluginService will shutdown and " +
                    "future events will be ignored", e);
        }
        return false;
    }

    private void trySendBuild(final HttpMethod httpMethod) throws IOException, InterruptedException {
        for (int i = 0; i <= _maxRetries; i++) {
            final HttpResponse<Build> response = _client.send(_build, Build.class, httpMethod);
            log.debug("Create build API returned responseCode: " + response.statusCode());
            if (200 == response.statusCode()) {
                if (0L == _build.getId()) {
                    CALLBACK_INVOKED.set(true);
                    _build = response.body();
                    _build.init();
                    log.debug("All tests in this run will be associated with buildId: " + _build.getId());
                }
                return;
            }
            Thread.sleep(100);
        }
    }

    public void flush() {
        final boolean hasTestFailures = _tests.values().stream()
                .anyMatch(x -> x.getResult().equals(Result.FAILED.getResult()));
        final Result buildResult = hasTestFailures ? Result.FAILED : Result.PASSED;
        _build.complete(buildResult);
        _client.retryHandler().sendWithRetries(_wrappedResponses);
        try {
            trySendBuild(HttpMethod.PUT);
        } catch (final IOException | InterruptedException e) {
            CALLBACK_INVOKED.set(false);
            log.error("Failed to send Build. PluginService will shutdown and " +
                    "future events will be ignored", e);
        }
    }

    public void afterTest(final Test test) throws IOException {
        if (!CALLBACK_INVOKED.get()) {
            return;
        }
        _tests.putIfAbsent(test.getClientId(), test);
        _build.updateStats(test);
        updateAttributes(test);

        final WrappedResponseAsync<Test> wrapper = new WrappedResponseAsync<>(test);
        _wrappedResponses.put(test.getClientId().toString(), wrapper);
        wrapper.setResponse(_client.sendAsync(wrapper.getEntity(), Test.class));
    }

    private void updateAttributes(final Test test) {
        test.setBuildId(_build.getId());
        _build.addTags(test.getTags());
        if (null != test.getChildren()) {
            for (final Test child : test.getChildren()) {
                updateAttributes(child);
            }
        }
    }

}
