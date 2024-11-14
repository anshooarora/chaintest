package com.aventstack.chaintest.generator;

import com.aventstack.chaintest.conf.ConfigurationManager;
import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChainTestEmailGenerator extends FileGenerator implements Generator {

    private static final Logger log = LoggerFactory.getLogger(ChainTestEmailGenerator.class);
    private static final String NAME = ChainTestEmailGenerator.class.getSimpleName();
    private static final String EMAIL_CLIENT_ENABLED = "chaintest.generator.email.enabled";
    private static final AtomicBoolean ENABLED = new AtomicBoolean();
    private static final String TEMPLATE_DIR = "email/";
    private static final String INDEX = "index.ftl";
    private static final String OUT_FILE = "target/chaintest/Email.html";

    private Build _build;
    private String _source;

    public String getSource() {
        return _source;
    }

    @Override
    public void start(final Optional<Map<String, String>> config, final String testRunner, final Build build) {
        final Map<String, String> configuration = config.orElse(ConfigurationManager.getConfig());
        if (configuration == null) {
            log.error("Unable to load ChainTestSimpleGenerator configuration, generator will now shutdown and no output will be produced");
            return;
        }

        final String enabled = configuration.get(EMAIL_CLIENT_ENABLED);
        if (!Boolean.parseBoolean(enabled)) {
            log.debug("{} Generator was not enabled. To enable, set property {}=true in your configuration", NAME, EMAIL_CLIENT_ENABLED);
            return;
        }

        log.trace("Start was called for testRunner: {}", testRunner);
        _build = build;
        try {
            cacheTemplate(ChainTestEmailGenerator.class, TEMPLATE_DIR, INDEX);
            ENABLED.set(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void flush(final Map<UUID, Test> tests) {
        if (tests.isEmpty() || !ENABLED.get()) {
            return;
        }
        _source = processTemplate(Map.of("build", _build, "tests", tests.values()), OUT_FILE);
    }

    @Override
    public void afterTest(final Test test, final Optional<Throwable> throwable) { }

    @Override
    public void executionFinished() { }

}
