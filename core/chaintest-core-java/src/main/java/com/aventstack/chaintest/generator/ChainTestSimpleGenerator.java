package com.aventstack.chaintest.generator;

import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Embed;
import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.util.DateTimeUtil;
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

public class ChainTestSimpleGenerator extends FileGenerator implements Generator {

    private static final Logger log = LoggerFactory.getLogger(ChainTestSimpleGenerator.class);
    private static final String GENERATOR_NAME = "simple";
    private static final String BASE_PROPERTY = "chaintest.generator.simple";
    private static final String PROP_ENABLED = BASE_PROPERTY + ".enabled";
    private static final String PROP_OUT_FILE_NAME = BASE_PROPERTY + ".output-file";
    private static final String PROP_SAVE_OFFLINE = BASE_PROPERTY + ".offline";
    private static final String PROP_DATETIME_FORMAT = BASE_PROPERTY + ".datetime-format";
    private static final String PROP_DOCUMENT_TITLE = BASE_PROPERTY + ".document-title";
    private static final String PROP_JS = BASE_PROPERTY + ".js";
    private static final String PROP_CSS = BASE_PROPERTY + ".css";
    private static final String BASE_TEMPLATE_NAME = "index.ftl";
    private static final String DEFAULT_OUT_FILE_NAME = "Simple.html";
    private static final String DEFAULT_OUT_DIR = "target/chaintest/";
    private static final String RESOURCES_DIR = "/resources";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss a";

    private Build _build;
    private String _projectName;
    private File _outFile;
    private String _datetimeFormat;
    private String _documentTitle;
    private String _js;
    private String _css;
    private boolean _offline;

    @Override
    public void start(final Optional<Map<String, String>> config, final String testRunner, final Build build) {
        if (config.isEmpty()) {
            log.debug("Unable to load {} configuration, generator will now shutdown and no output will be produced",
                    ChainTestSimpleGenerator.class.getSimpleName());
            return;
        }

        final String enabled = config.get().get(PROP_ENABLED);
        if (!Boolean.parseBoolean(enabled)) {
            log.debug("{} generator was not enabled. To enable, set property {}=true in your configuration", GENERATOR_NAME, PROP_ENABLED);
            return;
        }

        String outputFileName = config.get().get(PROP_OUT_FILE_NAME);
        if (null == outputFileName || outputFileName.isEmpty()) {
            outputFileName = DEFAULT_OUT_DIR + DEFAULT_OUT_FILE_NAME;
        }
        if (!(outputFileName.endsWith("htm") || outputFileName.endsWith("html"))) {
            outputFileName += "/" + DEFAULT_OUT_FILE_NAME;
        }
        _outFile = new File(outputFileName);

        _offline = Boolean.parseBoolean(config.get().get(PROP_SAVE_OFFLINE));
        if (_offline) {
            saveResources();
        }

        _projectName = config.get().get(ChainTestPropertyKeys.PROJECT_NAME);
        _projectName = null == _projectName ? "" : _projectName;
        _datetimeFormat = config.get().get(PROP_DATETIME_FORMAT);
        _datetimeFormat = null == _datetimeFormat || _datetimeFormat.isBlank() || !DateTimeUtil.isPatternValid(_datetimeFormat)
                ? DATETIME_FORMAT : _datetimeFormat;
        _documentTitle = config.get().get(PROP_DOCUMENT_TITLE);
        _documentTitle = null == _documentTitle ? ChainTestPropertyKeys.CHAINTEST : _documentTitle;
        _js = config.get().get(PROP_JS);
        _css = config.get().get(PROP_CSS);
        _build = build;

        log.trace("Start was called for testRunner: {}", testRunner);
        try {
            cacheTemplate(ChainTestSimpleGenerator.class, GENERATOR_NAME, BASE_TEMPLATE_NAME);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveResources() {
        final URL url = ChainTestSimpleGenerator.class.getResource(GENERATOR_NAME);
        if (null == url) {
            log.error("URL Resolved to an unknown path for class {} and resource {}",
                    ChainTestSimpleGenerator.class.getName(), GENERATOR_NAME);
            return;
        }

        final File parentDir = _outFile.getParentFile();
        new File(parentDir.getPath() + RESOURCES_DIR).mkdirs();

        try {
            final File dir = new File(url.toURI());
            for (final File f : dir.listFiles()) {
                if (f.getName().endsWith("ftl")) {
                    continue;
                }
                log.trace("Copying classpath resource {}", f.getPath());
                IOUtil.copyClassPathResource(ChainTestSimpleGenerator.class,
                        GENERATOR_NAME + "/" + f.getName(),
                        parentDir.getPath() + RESOURCES_DIR + "/" + f.getName());
            }
        } catch (final URISyntaxException e) {
            log.error("Failed to construct URI from url {}", url);
        }
    }

    public void flush(final List<Test> tests) {
        final File resourceDir = new File(_outFile.getParentFile().getPath() + RESOURCES_DIR);
        saveEmbeds(tests, resourceDir);
        processTemplate(Map.of("build", _build,
                "tests", tests,
                "config", Map.of(
                        "documentTitle", _documentTitle,
                        "projectName", _projectName,
                        "offline", _offline,
                        "datetimeFormat", _datetimeFormat,
                        "js", _js,
                        "css", _css)), _outFile);
    }

    private void saveEmbeds(final List<Test> tests, final File resourceDir) {
        for (final Test test : tests) {
            for (final Embed embed : test.getEmbeds()) {
                final File toFile = embed.makePath(resourceDir);
                try {
                    embed.save(toFile);
                } catch (final Exception e) {
                    log.error("Failed to save embed for test {} to resource dir {}", test.getName(), resourceDir.getPath());
                }
            }
            if (!test.getChildren().isEmpty()) {
                saveEmbeds(test.getChildren(), resourceDir);
            }
        }
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
