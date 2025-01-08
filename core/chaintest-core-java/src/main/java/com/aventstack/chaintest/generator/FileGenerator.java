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

public abstract class FileGenerator {

    private static final Logger log = LoggerFactory.getLogger(FileGenerator.class);

    private Template _template;
    private Class<?> _classForTemplateLoading;
    private String _basePackagePath;
    private String _templateName;

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

}
