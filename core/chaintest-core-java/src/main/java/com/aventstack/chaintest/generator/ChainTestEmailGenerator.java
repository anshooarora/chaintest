package com.aventstack.chaintest.generator;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public class ChainTestEmailGenerator extends FileGenerator implements Generator {

    private static final Logger log = LoggerFactory.getLogger(ChainTestEmailGenerator.class);
    private static final String NAME = "email";
    private static final String EMAIL_CLIENT_ENABLED = "chaintest.generator.email.enabled";
    private static final String TEMPLATE_DIR = "email/";
    private static final String INDEX = "index.ftl";
    private static final String OUT_FILE = "target/chaintest/Email.html";

    private boolean _started;
    private Build _build;
    private String _source;

    public String getSource() {
        return _source;
    }

    @Override
    public void start(final Optional<Map<String, String>> config, final String testRunner, final Build build) {
        if (config.isEmpty()) {
            log.debug("Unable to load {} configuration, generator will now shutdown and no output will be produced",
                    ChainTestEmailGenerator.class.getSimpleName());
            return;
        }

        final String enabled = config.get().get(EMAIL_CLIENT_ENABLED);
        if (!Boolean.parseBoolean(enabled)) {
            log.debug("{} Generator was not enabled. To enable, set property {}=true in your configuration", NAME, EMAIL_CLIENT_ENABLED);
            return;
        }

        log.trace("Start was called for testRunner: {}", testRunner);
        _build = build;
        try {
            cacheTemplate(ChainTestEmailGenerator.class, TEMPLATE_DIR, INDEX);
            _started = true;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean started() {
        return _started;
    }

    public void flush(final Queue<Test> tests) {
        if (null == _build || null == tests || tests.isEmpty() || _build.getRunStats().isEmpty()) {
            log.debug("No tests to process, skipping flush");
            return;
        }
        _source = processTemplate(Map.of("build", _build, "tests", tests), OUT_FILE);
    }

    @Override
    public void afterTest(final Test test, final Optional<Throwable> throwable) { }

    @Override
    public void executionFinished() { }

    @Override
    public String getName() {
        return NAME;
    }

}
