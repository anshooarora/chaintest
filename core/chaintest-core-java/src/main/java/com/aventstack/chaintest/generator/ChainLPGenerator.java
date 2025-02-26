package com.aventstack.chaintest.generator;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.ExecutionStage;
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
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChainLPGenerator implements Generator {

    private static final Logger log = LoggerFactory.getLogger(ChainLPGenerator.class);
    private static final String NAME = "chainlp";
    private static final String HTTP_CLIENT_ENABLED = "chaintest.generator.chainlp.enabled";
    private static final String SEND_EMBEDS = "chaintest.generator.chainlp.persist-embeds";
    private static final ConcurrentHashMap<String, WrappedResponseAsync<Test>> _wrappedResponses = new ConcurrentHashMap<>();
    private static final AtomicBoolean CALLBACK_INVOKED = new AtomicBoolean();

    private static ChainLPGenerator instance;

    private String _testRunner;
    private ChainTestApiClient _client;
    private Build _build;
    private Queue<Test> _tests;
    private boolean _sendEmbeds = true;

    public ChainLPGenerator(final String testRunner) {
        _testRunner = testRunner;
        instance = this;
    }

    public ChainLPGenerator() {
        this("");
    }

    public void client(final ChainTestApiClient client) {
        _client = client;
    }

    public ChainTestApiClient getClient() {
        return _client;
    }

    public ChainLPGenerator getInstance() {
        return instance;
    }

    public Build getBuild() {
        return _build;
    }

    @Override
    public void start(final Optional<Map<String, String>> config, final String testRunner, final Build build) {
        _testRunner = testRunner;
        if (started()) {
            return;
        }

        if (config.isEmpty()) {
            log.debug("Unable to load {} configuration, generator will now shutdown and no output will be produced",
                    ChainLPGenerator.class.getSimpleName());
            return;
        }

        final String enabled = config.get().get(HTTP_CLIENT_ENABLED);
        if (!Boolean.parseBoolean(enabled)) {
            log.debug("Http Generator was not enabled. To enable Http generator, set property {}=true in your configuration", HTTP_CLIENT_ENABLED);
            return;
        }

        if (null == _client) {
            try {
                _client = new ChainTestApiClient();
            } catch (final Exception e) {
                log.error("Failed to create an instance of {}", ChainTestApiClient.class, e);
                return;
            }
        }

        log.trace("Starting new build, but events will only be sent to API if build is successfully created");

        final String projectName = _client.config().getConfig().getOrDefault(ChainTestPropertyKeys.PROJECT_NAME, "");
        Build buildReq = new Build(projectName, _testRunner);
        buildReq.setSystemInfo(build.getSystemInfo());

        try {
            final HttpResponse<Build> response = _client.retryHandler().trySend(buildReq, Build.class, HttpMethod.POST);
            if (null != response && 200 == response.statusCode()) {
                buildReq = response.body();
                _build = build;
                _build.setId(buildReq.getId());
                _build.setProjectId(buildReq.getProjectId());
                CALLBACK_INVOKED.set(true);
                log.debug("All tests in this run will be associated with buildId: {}", _build.getId());

                final String sendEmbeds = config.get().get(SEND_EMBEDS);
                if (null != sendEmbeds && !sendEmbeds.isEmpty()) {
                    _sendEmbeds = Boolean.parseBoolean(sendEmbeds);
                }
            }
        } catch (final IOException | InterruptedException e) {
            log.debug("Failed to send Build. PluginService will shutdown and " +
                    "future events will be ignored", e);
        }
    }

    @Override
    public boolean started() {
        return CALLBACK_INVOKED.get();
    }

    @Override
    public void executionFinished() {
        if (!started()) {
            return;
        }
        _build.setExecutionStage(ExecutionStage.FINISHED);
        flush(_tests);
    }

    @Override
    public void flush(final Queue<Test> tests) {
        if (!started()) {
            return;
        }
        _tests = tests;
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
        if (!started()) {
            return;
        }

        updateAttributes(test);

        if (!_sendEmbeds) {
            setEmbedStorableState(test);
        }

        final WrappedResponseAsync<Test> wrapper = new WrappedResponseAsync<>(test);
        _wrappedResponses.put(test.getClientId().toString(), wrapper);
        try {
            final CompletableFuture<HttpResponse<Test>> future = _client.sendAsync(wrapper.getEntity(), Test.class);
            wrapper.setResponseFuture(future);
        } catch (final IOException e) {
            log.error("Exception sending test", e);
        }
    }

    private void setEmbedStorableState(final Test test) {
        test.getEmbeds().forEach(embed -> embed.setStore(false));
        test.getChildren().forEach(this::setEmbedStorableState);
    }

    private void updateAttributes(final Test test) {
        test.setBuildId(_build.getId());
        test.setProjectId(_build.getProjectId());
        _build.addTags(test.getTags());
        test.getChildren().forEach(this::updateAttributes);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
