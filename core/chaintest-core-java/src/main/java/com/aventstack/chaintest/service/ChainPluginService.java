package com.aventstack.chaintest.service;

import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.generator.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChainPluginService {

    private static final Logger log = LoggerFactory.getLogger(ChainPluginService.class);
    private static final String PROJECT_ID = "chaintest.project.id";
    private static final String PROJECT_NAME = "chaintest.project.name";
    private static final ConcurrentHashMap<UUID, Test> _tests = new ConcurrentHashMap<>();

    public static ChainPluginService INSTANCE;

    private final String _testRunner;
    private final List<Generator> _generators = new ArrayList<>(2);

    public ChainPluginService(final String testRunner) throws IOException {
        _testRunner = testRunner;
    }

    public void register(final Generator generator) {
        _generators.add(generator);
    }

    public void start() {
        _generators.forEach(x -> x.start(_testRunner));
    }

    public void afterTest(final Test test, final Optional<Throwable> throwable) throws IOException {
        test.complete(throwable);
        _tests.putIfAbsent(test.getClientId(), test);
    }

    public void flush() {
        _generators.forEach(x -> x.flush(_tests));
    }

    public void executionFinished() {
        _generators.forEach(Generator::executionFinished);
    }

}
