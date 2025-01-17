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
    private static final String NAME = "email";
    private static final String BASE_PROPERTY = "chaintest.generator.email";
    private static final String PROP_ENABLED = BASE_PROPERTY + ".enabled";
    private static final String PROP_OUT_FILE_NAME = BASE_PROPERTY + ".output-file";
    private static final String DEFAULT_OUT_DIR = "target/chaintest/";
    private static final String DEFAULT_OUT_FILE_NAME = "Email.html";
    private static final String TEMPLATE_DIR = "email/";
    private static final String INDEX = "index.ftl";

    private boolean _started;
    private Build _build;
    private String _source;
    private File _outFile;

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

        final String enabled = config.get().get(PROP_ENABLED);
        if (!Boolean.parseBoolean(enabled)) {
            log.debug("{} Generator was not enabled. To enable, set property {}=true in your configuration", NAME, PROP_ENABLED);
            return;
        }

        String outputFileName = Optional.ofNullable(config.get().get(PROP_OUT_FILE_NAME))
                .filter(name -> !name.isEmpty())
                .orElse(DEFAULT_OUT_DIR + DEFAULT_OUT_FILE_NAME);
        if (!(outputFileName.endsWith("htm") || outputFileName.endsWith("html"))) {
            outputFileName += "/" + DEFAULT_OUT_FILE_NAME;
        }
        _outFile = new File(outputFileName);

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
        _source = processTemplate(Map.of("build", _build, "tests", tests), _outFile);
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
