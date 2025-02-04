package com.aventstack.chaintest.generator;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public class ChainTestEmailGenerator extends FileGenerator implements Generator {

    private static final Logger log = LoggerFactory.getLogger(ChainTestEmailGenerator.class);
    private static final String GENERATOR_NAME = "email";
    private static final String BASE_PROPERTY = "chaintest.generator.email";
    private static final String PROP_ENABLED = BASE_PROPERTY + ".enabled";
    private static final String TEMPLATE_DIR = "email/";
    private static final String INDEX = "index.ftl";

    private Map<String, String> _config;
    private boolean _started;
    private Build _build;
    private String _source;
    private File _outFile;

    public ChainTestEmailGenerator() {
        super(GENERATOR_NAME);
    }

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

        _config = config.get();

        final String enabled = _config.get(PROP_ENABLED);
        if (!Boolean.parseBoolean(enabled)) {
            log.debug("{} Generator was not enabled. To enable, set property {}=true in your configuration", GENERATOR_NAME, PROP_ENABLED);
            return;
        }

        _outFile = getOutFile(_config);

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
        _outFile = getOutFile(_config);
        if (null == _build || null == tests || tests.isEmpty() || _build.getRunStats().isEmpty()) {
            log.debug("No tests to process, skipping flush");
            return;
        }
        _source = processTemplate(Map.of("build", _build, "tests", tests), _outFile);
    }

    @Override
    public void afterTest(final Test test, final Optional<Throwable> throwable) { }

    @Override
    public void executionFinished() { }

    @Override
    public String getName() {
        return GENERATOR_NAME;
    }

}
