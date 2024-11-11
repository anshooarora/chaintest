package com.aventstack.chaintest.generator;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;

public class FreemarkerConfig {

    private static final String BASE_PACKAGE_PATH = "/";

    public Configuration getConfig(final Class<?> classForTemplateLoading, final String basePackagePath) throws IOException {
        final Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setClassForTemplateLoading(classForTemplateLoading, basePackagePath);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        return cfg;
    }

    public Configuration getConfig(final Class<?> classForTemplateLoading) throws IOException {
        return getConfig(classForTemplateLoading, BASE_PACKAGE_PATH);
    }

}
