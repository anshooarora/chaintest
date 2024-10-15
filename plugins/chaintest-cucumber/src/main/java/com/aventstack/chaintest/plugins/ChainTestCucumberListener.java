package com.aventstack.chaintest.plugins;

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
import io.cucumber.plugin.event.TestStepStarted;
import io.cucumber.plugin.event.WriteEvent;

public class ChainTestCucumberListener implements EventListener {

    public ChainTestCucumberListener(final String ignored) { }

    @Override
    public void setEventPublisher(final EventPublisher publisher) {
        publisher.registerHandlerFor(TestSourceRead.class, testSourceReadHandler);
        publisher.registerHandlerFor(TestCaseStarted.class, caseStartedHandler);
        publisher.registerHandlerFor(TestCaseFinished.class, caseFinishedHandler);
        publisher.registerHandlerFor(TestStepStarted.class, stepStartedHandler);
        publisher.registerHandlerFor(TestStepFinished.class, stepFinishedHandler);
        publisher.registerHandlerFor(EmbedEvent.class, embedEventhandler);
        publisher.registerHandlerFor(WriteEvent.class, writeEventhandler);
        publisher.registerHandlerFor(TestRunFinished.class, runFinishedHandler);
    }

    private final EventHandler<TestSourceRead> testSourceReadHandler = event -> {
        System.out.println(event);
    };

    private final EventHandler<TestCaseStarted> caseStartedHandler = event -> {
        System.out.println(event.getTestCase().getName());
    };

    private final EventHandler<TestCaseFinished> caseFinishedHandler = event -> {
        System.out.println(event.getResult().getStatus());
    };

    private final EventHandler<TestStepStarted> stepStartedHandler = event -> {

    };

    private final EventHandler<TestStepFinished> stepFinishedHandler = event -> {
        System.out.println("-- " + ((PickleStepTestStep) event.getTestStep()).getStep().getText());
    };

    private final EventHandler<EmbedEvent> embedEventhandler = event -> {
        System.out.println(event);
    };

    private final EventHandler<WriteEvent> writeEventhandler = event -> {
        System.out.println(event);
    };

    private final EventHandler<TestRunFinished> runFinishedHandler = event -> {
        System.out.println(event);
    };

}
