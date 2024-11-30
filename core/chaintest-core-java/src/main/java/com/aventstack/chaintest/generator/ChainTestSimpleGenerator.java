package com.aventstack.chaintest.generator;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChainTestSimpleGenerator extends FileGenerator implements Generator {

    private static final Logger log = LoggerFactory.getLogger(ChainTestSimpleGenerator.class);
    private static final String NAME = "simple";
    private static final String SIMPLE_CLIENT_BASE_PROPERTY_NAME = "chaintest.generator.simple";
    private static final String SIMPLE_CLIENT_ENABLED = SIMPLE_CLIENT_BASE_PROPERTY_NAME + ".enabled";
    private static final String SIMPLE_CLIENT_OUT = SIMPLE_CLIENT_BASE_PROPERTY_NAME + ".out-dir";
    private static final AtomicBoolean ENABLED = new AtomicBoolean();
    private static final String INDEX = "index.ftl";
    private static final String OUT_FILE = "Simple.html";

    private Build _build;
    private String _outDir;

    @Override
    public void start(final Optional<Map<String, String>> config, final String testRunner, final Build build) {
        if (config.isEmpty()) {
            log.debug("Unable to load {} configuration, generator will now shutdown and no output will be produced",
                    ChainTestSimpleGenerator.class.getSimpleName());
            return;
        }

        final String enabled = config.get().get(SIMPLE_CLIENT_ENABLED);
        if (!Boolean.parseBoolean(enabled)) {
            log.debug("{} generator was not enabled. To enable, set property {}=true in your configuration", NAME, SIMPLE_CLIENT_ENABLED);
            return;
        }

        _outDir = config.get().get(SIMPLE_CLIENT_OUT);
        if (null == _outDir || _outDir.isEmpty()) {
            _outDir = null;
        }

        log.trace("Start was called for testRunner: {}", testRunner);
        _build = build;
        try {
            cacheTemplate(ChainTestSimpleGenerator.class, NAME, INDEX);
            ENABLED.set(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveResourcesLocal() {
        final URL url = ChainTestSimpleGenerator.class.getResource(NAME);
        if (null == url) {
            log.error("URL Resolved to an unknown path for class {} and resource {}",
                    ChainTestSimpleGenerator.class.getName(), NAME);
            return;
        }
        try {
            final File dir = new File(url.toURI());
            for (final File f : dir.listFiles()) {
                if (f.getName().endsWith("ftl")) {
                    continue;
                }
                log.trace("Copying classpath resource {}", f.getPath());
                IOUtil.copyClassPathResource(ChainTestSimpleGenerator.class,
                        NAME + "/" + f.getName(),
                        _outDir + f.getName());
            }
        } catch (final URISyntaxException e) {
            log.error("Failed to construct URI from url {}", url);
        }
    }

    public void flush(final List<Test> tests) {
        if (ENABLED.get()) {
            processTemplate(Map.of("build", _build, "tests", tests), OUT_FILE);
        }
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
