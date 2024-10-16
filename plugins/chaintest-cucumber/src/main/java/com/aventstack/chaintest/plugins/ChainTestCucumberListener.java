package com.aventstack.chaintest.plugins;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.http.ChainTestApiClient;
import com.aventstack.chaintest.http.WrappedResponseAsync;
import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.Tag;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EmbedEvent;
import io.cucumber.plugin.event.EventHandler;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestSourceRead;
import io.cucumber.plugin.event.TestStepFinished;
import io.cucumber.plugin.event.WriteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ChainTestCucumberListener implements EventListener {

    private static final Logger log = LoggerFactory.getLogger(ChainTestCucumberListener.class);
    private static final String TEST_RUNNER = "cucumber-jvm";
    private static final AtomicBoolean READY = new AtomicBoolean();
    private static Build _build = null;

    private final Map<UUID, Test> _scenarios = new HashMap<>();
    private final Map<URI, Test> _features = new HashMap<>();
    private final Set<URI> _requestsSent = new HashSet<>();
    private final Map<String, WrappedResponseAsync<Test>> _requests = new HashMap<>();
    private ChainTestApiClient _client;

    public ChainTestCucumberListener(final String ignored) {
        loadClient();
    }

    private void loadClient() {
        try {
            _client = new ChainTestApiClient();

            final Build build = new Build(TEST_RUNNER);
            final HttpResponse<Build> response = _client.send(build, Build.class);
            log.debug("Create build API returned responseCode: " + response.statusCode());
            if (200 == response.statusCode()) {
                _build = response.body();
                READY.set(true);
                log.debug("All tests in this run will be associated with buildId: " + _build.getId());
            }
        } catch (final Exception e) {
            log.error("Either client was not loaded properly or failed to communicate with ChainTest service", e);
        }
    }

    @Override
    public void setEventPublisher(final EventPublisher publisher) {
        if (!READY.get()) {
            return;
        }
        publisher.registerHandlerFor(TestSourceRead.class, testSourceReadHandler);
        publisher.registerHandlerFor(TestCaseStarted.class, caseStartedHandler);
        publisher.registerHandlerFor(TestCaseFinished.class, caseFinishedHandler);
        publisher.registerHandlerFor(TestStepFinished.class, stepFinishedHandler);
        publisher.registerHandlerFor(EmbedEvent.class, embedEventhandler);
        publisher.registerHandlerFor(WriteEvent.class, writeEventhandler);
        publisher.registerHandlerFor(TestRunFinished.class, runFinishedHandler);
    }

    private final EventHandler<TestSourceRead> testSourceReadHandler = event -> {
        final Optional<Feature> container = getFeature(event);
        if (container.isPresent() && !_features.containsKey(event.getUri()) && null != _build) {
            final Feature feature = container.get();
            final List<String> tags = feature.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toUnmodifiableList());
            final Test test = new Test(_build.getId(),
                    feature.getName(),
                    Optional.of(feature.getClass()),
                    tags);
            _features.put(event.getUri(), test);
        }
        sendFeaturesAsync();
    };

    private void sendFeaturesAsync() {
        for (final Map.Entry<URI, Test> entry : _features.entrySet()) {
            if (!_requestsSent.contains(entry.getKey()) && null != entry.getValue().getChildren()
                    && !entry.getValue().getChildren().isEmpty()) {
                _requestsSent.add(entry.getKey());
                final WrappedResponseAsync<Test> wrapper = new WrappedResponseAsync<>(entry.getValue());
                _requests.put(entry.getKey().getPath(), wrapper);
                try {
                    wrapper.setResponse(_client.sendAsync(wrapper.getEntity(), Test.class));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        _client.retryHandler().sendWithRetriesAsync(_requests);
    }

    private Optional<Feature> getFeature(final TestSourceRead event) {
        final GherkinParser parser = GherkinParser.builder()
                .includePickles(false)
                .includeSource(false)
                .build();
        try {
            final Optional<Envelope> envelope = parser.parse(Paths.get(event.getUri().getPath()))
                    .findAny();
            if (envelope.isEmpty() || envelope.get().getGherkinDocument().isEmpty()) {
                log.error("No features were found in " + event.getUri());
                return Optional.empty();
            }
            final GherkinDocument document = envelope.get().getGherkinDocument().get();
            if (document.getFeature().isEmpty()) {
                log.error("Feature file " + event.getUri() + " does not contain a Feature");
            }
            return document.getFeature();
        } catch (final IOException e) {
            log.error("Failed to load feature file " + event.getUri(), e);
        }
        return Optional.empty();
    }

    private final EventHandler<TestCaseStarted> caseStartedHandler = event -> {
        final Test scenario = new Test(_build.getId(),
                event.getTestCase().getName(),
                Optional.of(event.getClass()),
                event.getTestCase().getTags());
        _features.get(event.getTestCase().getUri()).addChild(scenario);
        _scenarios.put(event.getTestCase().getId(), scenario);
    };

    private final EventHandler<TestCaseFinished> caseFinishedHandler = event -> {
        final Test scenario = _scenarios.get(event.getTestCase().getId());
        scenario.complete(event.getResult().getError());
        scenario.setResult(event.getResult().getStatus().name());
    };

    private final EventHandler<TestStepFinished> stepFinishedHandler = event -> {
        final Test step = new Test(_build.getId(),
                ((PickleStepTestStep) event.getTestStep()).getStep().getText(),
                Optional.of(event.getClass()));
        _scenarios.get(event.getTestCase().getId()).addChild(step);
        step.complete(event.getResult().getError());
        step.setResult(event.getResult().getStatus().name());
    };

    private final EventHandler<EmbedEvent> embedEventhandler = event -> {
        System.out.println(event);
    };

    private final EventHandler<WriteEvent> writeEventhandler = event -> {
        System.out.println(event);
    };

    private final EventHandler<TestRunFinished> runFinishedHandler = event -> {
        System.out.println(event);
        sendFeaturesAsync();
    };

}
