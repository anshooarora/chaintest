package com.aventstack.chaintest.service;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.generator.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChainPluginService {

    private static final Logger log = LoggerFactory.getLogger(ChainPluginService.class);
    private static final ConcurrentHashMap<UUID, Test> _tests = new ConcurrentHashMap<>();

    public static ChainPluginService INSTANCE;

    private final Build _build;
    private final String _testRunner;
    private final List<Generator> _generators = new ArrayList<>(2);

    public ChainPluginService(final String testRunner) {
        INSTANCE = this;
        _build = new Build(testRunner);
        _testRunner = testRunner;
    }

    public void register(final Generator generator) {
        _generators.add(generator);
    }

    public void register(final Collection<Generator> generators) {
        _generators.addAll(generators);
    }

    public void start() {
        _generators.forEach(x -> x.start(_testRunner, _build));
    }

    public void afterTest(final Test test, final Optional<Throwable> throwable) {
        test.complete(throwable);
        _tests.putIfAbsent(test.getClientId(), test);
        _build.updateStats(test);
        _generators.forEach(x -> x.afterTest(test, throwable));
    }

    public void flush() {
        _build.complete();
        _generators.forEach(x -> x.flush(_tests));
    }

    public void executionFinished() {
        flush();
        _generators.forEach(Generator::executionFinished);
    }

}
