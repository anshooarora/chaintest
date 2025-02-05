package com.aventstack.chaintest.generator;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerConfig {

    private static final String BASE_PACKAGE_PATH = "/";

    public Configuration getConfig(final Class<?> classForTemplateLoading, final String basePackagePath) {
        final Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        cfg.setClassForTemplateLoading(classForTemplateLoading, basePackagePath);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        return cfg;
    }

    public Configuration getConfig(final Class<?> classForTemplateLoading) {
        return getConfig(classForTemplateLoading, BASE_PACKAGE_PATH);
    }

}
