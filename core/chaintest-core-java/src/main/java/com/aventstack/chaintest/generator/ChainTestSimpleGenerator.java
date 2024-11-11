package com.aventstack.chaintest.generator;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ChainTestSimpleGenerator extends FileGenerator implements Generator {

    private static final Logger log = LoggerFactory.getLogger(ChainTestSimpleGenerator.class);
    private static final String TEMPLATE_DIR = "simple/";
    private static final String INDEX = "index.ftl";
    private static final String OUT_FILE = "target/chaintest/Index.html";

    private Build _build;

    @Override
    public void start(final String testRunner, final Build build) {
        log.debug("Start was called for testRunner: {}", testRunner);
        _build = build;
        try {
            cacheTemplate(ChainTestSimpleGenerator.class, TEMPLATE_DIR, INDEX);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void flush(final Map<UUID, Test> tests) {
        processTemplateToFile(Map.of("build", _build, "tests", tests.values()), OUT_FILE);
    }

    @Override
    public void afterTest(final Test test, final Optional<Throwable> throwable) { }

    @Override
    public void executionFinished() { }

}
