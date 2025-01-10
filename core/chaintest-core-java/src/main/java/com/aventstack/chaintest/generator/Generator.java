package com.aventstack.chaintest.generator;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public interface Generator {

    void start(final Optional<Map<String, String>> config, final String testRunner, final Build build);

    boolean started();

    void afterTest(final Test test, final Optional<Throwable> throwable);

    void flush(Queue<Test> tests);

    void executionFinished();

    String getName();

}
