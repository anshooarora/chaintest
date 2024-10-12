package com.aventstack.chaintest.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * ChainTest allows you to load externalized configuration from properties files,
 * environment variables and system properties using the following order:
 *
 * <ol>
 *     <li>application.properties</li>
 *     <li>application-test.properties</li>
 *     <li>test.properties</li>
 *     <li>config.properties</li>
 *     <li>chaintest.properties</li>
 *     <li>OS environment variables</li>
 *     <li>System.getProperties()</li>
 * </ol>
 */
public class Configuration {

    private static final String APP_NAME = "chaintest";
    private static final String[] RESOURCES = new String[] {
            "application.properties",
            "application-test.properties",
            "test.properties",
            "config.properties",
            "chaintest.properties"
    };

    private final Map<String, String> _config = new HashMap<>();

    public Map<String, String> getConfig() {
        return _config;
    }

    public void load() throws IOException {
        for (final String resource : RESOURCES) {
            loadFromClasspathResource(_config, resource);
        }

        System.getenv().forEach((k, v) -> {
            if (k.startsWith(APP_NAME)) {
                _config.put(k, v);
            }
        });

        loadFromProperties(_config, System.getProperties());
    }

    public void loadFromClasspathResource(final Map<String, String> config, final String resource) throws IOException {
        final Properties properties = new Properties();
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (null != is) {
            properties.load(is);
            loadFromProperties(config, properties);
        }
    }

    public void loadFromProperties(final Map<String, String> config, final Properties properties) {
        properties.stringPropertyNames().stream()
                .filter(x -> x.startsWith(APP_NAME))
                .forEach(x -> config.put(x, properties.getProperty(x)));
    }

}
