package com.aventstack.chaintest.generator;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.ExecutionStage;
import com.aventstack.chaintest.domain.Result;
import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.http.ChainTestApiClient;
import com.aventstack.chaintest.http.HttpMethod;
import com.aventstack.chaintest.http.WrappedResponseAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChainTestServiceClient implements Generator {

    private static final Logger log = LoggerFactory.getLogger(ChainTestServiceClient.class);
    private static final String PROJECT_ID = "chaintest.project.id";
    private static final String PROJECT_NAME = "chaintest.project.name";
    private static final ConcurrentHashMap<String, WrappedResponseAsync<Test>> _wrappedResponses = new ConcurrentHashMap<>();
    private static final AtomicBoolean CALLBACK_INVOKED = new AtomicBoolean();

    public static ChainTestServiceClient INSTANCE;

    private final ChainTestApiClient _client;
    private String _testRunner;
    private Build _build;

    public ChainTestServiceClient(final ChainTestApiClient client, final String testRunner) {
        _client = client;
        _testRunner = testRunner;
        INSTANCE = this;
    }

    public ChainTestServiceClient(final String testRunner) throws IOException {
        this(new ChainTestApiClient(), testRunner);
    }

    public ChainTestServiceClient() throws IOException {
        this("");
    }

    public boolean ready() {
        return CALLBACK_INVOKED.get();
    }

    public ChainTestApiClient getClient() {
        return _client;
    }

    public Build getBuild() {
        return _build;
    }

    public void start(final String testRunner, final Build ignored) {
        log.trace("Starting new build, but events will only be sent to API if build is successfully created");

        final int projectId = Integer.parseInt(_client.config().get(PROJECT_ID));
        final String projectName = _client.config().getConfig().getOrDefault(PROJECT_NAME, "");
        if (projectId > 0) {
            _build = new Build(projectId, _testRunner);
        } else if (!projectName.isBlank()) {
            _build = new Build(projectName, _testRunner);
        } else {
            _build = new Build(_testRunner);
        }

        try {
            final HttpResponse<Build> response = _client.retryHandler().trySend(_build, Build.class, HttpMethod.POST);
            if (200 == response.statusCode()) {
                _build = response.body();
                CALLBACK_INVOKED.set(true);
                log.debug("All tests in this run will be associated with buildId: {}", _build.getId());
            }
        } catch (final Exception e) {
            log.debug("Failed to send Build. PluginService will shutdown and " +
                    "future events will be ignored", e);
        }
    }

    @Override
    public void executionFinished() {
        _build.setExecutionStage(ExecutionStage.FINISHED);
        flush(Map.of());
    }

    @Override
    public void flush(final Map<UUID, Test> tests) {
        if (!ready()) {
            return;
        }

        final boolean hasTestFailures = tests.values().stream()
                .anyMatch(x -> x.getResult().equals(Result.FAILED.getResult()));
        final Result buildResult = hasTestFailures ? Result.FAILED : Result.PASSED;
        _build.complete(buildResult);
        final Map<String, WrappedResponseAsync<Test>> failures = _client.retryHandler().sendWithRetries(_wrappedResponses);
        try {
            if (!failures.isEmpty()) {
                throw new IllegalStateException("Failed to transfer " + failures.size() + " tests");
            }
            _client.retryHandler().trySend(_build, Build.class, HttpMethod.PUT);
        } catch (final Exception e) {
            CALLBACK_INVOKED.set(false);
            log.error("Failed to send test(s) or build. PluginService will shutdown and " +
                    "future events will be ignored", e);
        }
    }

    @Override
    public void afterTest(final Test test, final Optional<Throwable> throwable) {
        if (!ready()) {
            return;
        }

        test.complete(throwable);
        _build.updateStats(test);
        updateAttributes(test);

        final WrappedResponseAsync<Test> wrapper = new WrappedResponseAsync<>(test);
        _wrappedResponses.put(test.getClientId().toString(), wrapper);
        try {
            final CompletableFuture<HttpResponse<Test>> future = _client.sendAsync(wrapper.getEntity(), Test.class);
            wrapper.setResponseFuture(future);
        } catch (final IOException e) {
            log.error("Exception sending test", e);
        }
    }

    private void updateAttributes(final Test test) {
        test.setBuildId(_build.getId());
        _build.addTags(test.getTags());
        if (null != test.getChildren()) {
            test.getChildren().forEach(this::updateAttributes);
        }
    }

}