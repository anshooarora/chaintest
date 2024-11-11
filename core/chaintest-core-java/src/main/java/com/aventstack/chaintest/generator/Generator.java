package com.aventstack.chaintest.generator;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Test;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface Generator {

    void start(final String testRunner, final Build build);

    void afterTest(final Test test, final Optional<Throwable> throwable);

    void flush(Map<UUID, Test> tests);

    void executionFinished();

}
