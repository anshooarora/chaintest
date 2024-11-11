package com.aventstack.chaintest.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public abstract class FileGenerator {

    private Template _template;

    protected void processTemplateToFile(final Map<String, Object> objectModel, final String outputFile) {
        final File file = new File(outputFile);
        final File dir = Files.isDirectory(file.toPath()) ? file : file.getParentFile();
        dir.mkdirs();

        try (final FileWriter out = new FileWriter(outputFile)) {
            _template.process(objectModel, out);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String processTemplateToString(final Map<String, Object> objectModel) {
        try (final StringWriter out = new StringWriter()) {
            _template.process(objectModel, out);
            return out.toString();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Template cacheTemplate(final Class<?> classForTemplateLoading, final String basePackagePath,
                                     final String templateName) throws IOException {
        if (_template == null) {
            final Configuration cfg = new FreemarkerConfig().getConfig(classForTemplateLoading, basePackagePath);
            _template = cfg.getTemplate(templateName);
        }
        return _template;
    }

}
