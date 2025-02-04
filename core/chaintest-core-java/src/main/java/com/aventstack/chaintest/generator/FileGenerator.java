package com.aventstack.chaintest.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

public abstract class FileGenerator {

    private static final Logger log = LoggerFactory.getLogger(FileGenerator.class);

    protected static final String BASE_PROPERTY = "chaintest.generator";
    protected static final String PROP_OUT_FILE_KEY = "output-file";
    protected static final String DEFAULT_OUT_DIR = "target/chaintest/";

    private final String _propOutFile;
    private final String _defaultOutFileName;
    private Template _template;
    private Class<?> _classForTemplateLoading;
    private String _basePackagePath;
    private String _templateName;

    protected FileGenerator(final String gen) {
        _propOutFile = BASE_PROPERTY + "." + gen + "." + PROP_OUT_FILE_KEY;
        _defaultOutFileName = gen + ".html";
    }

    protected String processTemplate(final Map<String, Object> objectModel, final File outputFile) {
        final File dir = Files.isDirectory(outputFile.toPath()) ? outputFile : outputFile.getParentFile();
        if (null != dir) {
            dir.mkdirs();
        }

        if (null == _template) {
            try {
                cacheTemplate(_classForTemplateLoading, _basePackagePath, _templateName);
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        try (final FileWriter out = new FileWriter(outputFile)) {
            _template.process(objectModel, out);
            return out.toString();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        } catch (final TemplateException e) {
            log.error("Error processing template", e);
            throw new RuntimeException(e);
        }
    }

    protected String processTemplate(final Map<String, Object> objectModel, final String outputFile) {
        return processTemplate(objectModel, new File(outputFile));
    }

    protected void cacheTemplate(final Class<?> classForTemplateLoading, final String basePackagePath,
                                 final String templateName) throws IOException {
        _classForTemplateLoading = classForTemplateLoading;
        _basePackagePath = basePackagePath;
        _templateName = templateName;
        if (_template == null) {
            log.debug("Loading template {} for class {} with basePackagePath {}", templateName, classForTemplateLoading, basePackagePath);
            final Configuration cfg = new FreemarkerConfig().getConfig(classForTemplateLoading, basePackagePath);
            _template = cfg.getTemplate(templateName);
        }
    }

    protected File getOutFile() {
        final String envOutFile = System.getenv(_propOutFile);
        if (null != envOutFile && !envOutFile.isEmpty()) {
            return new File(envOutFile);
        }
        final String sysOutFile = System.getProperty(_propOutFile);
        if (null != sysOutFile && !sysOutFile.isEmpty()) {
            return new File(sysOutFile);
        }
        return null;
    }

    protected File getOutFile(final Map<String, String> config) {
        final File outFile = getOutFile();
        if (null != outFile) {
            return outFile;
        }
        String outputFileName = Optional.ofNullable(config.get(_propOutFile))
                .filter(name -> !name.isEmpty())
                .orElse(DEFAULT_OUT_DIR + _defaultOutFileName);
        if (!(outputFileName.endsWith("htm") || outputFileName.endsWith("html"))) {
            outputFileName += "/" + _defaultOutFileName;
        }
        return new File(outputFileName);
    }

}
