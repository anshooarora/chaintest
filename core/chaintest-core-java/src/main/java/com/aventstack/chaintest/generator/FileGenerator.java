package com.aventstack.chaintest.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.util.Map;

public abstract class FileGenerator {

    private static final Logger log = LoggerFactory.getLogger(FileGenerator.class);

    private Template _template;

    protected String processTemplate(final Map<String, Object> objectModel, final File outputFile) {
        final File dir = Files.isDirectory(outputFile.toPath()) ? outputFile : outputFile.getParentFile();
        if (null != dir) {
            dir.mkdirs();
        }

        try (final FileWriter out = new FileWriter(outputFile)) {
            _template.process(objectModel, out);
            return out.toString();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String processTemplate(final Map<String, Object> objectModel, final String outputFile) {
        return processTemplate(objectModel, new File(outputFile));
    }

    protected void cacheTemplate(final Class<?> classForTemplateLoading, final String basePackagePath,
                                 final String templateName) throws IOException {
        if (_template == null) {
            log.debug("Loading template {} for class {} with basePackagePath {}", templateName, classForTemplateLoading, basePackagePath);
            final Configuration cfg = new FreemarkerConfig().getConfig(classForTemplateLoading, basePackagePath);
            _template = cfg.getTemplate(templateName);
        }
    }

}
