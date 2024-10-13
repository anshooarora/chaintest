package com.aventstack.chaintest.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(Configuration.class);
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
        log.trace("Starting load externalized configuration");
        for (final String resource : RESOURCES) {
            loadFromClasspathResource(resource);
        }

        System.getenv().forEach((k, v) -> {
            if (k.startsWith(APP_NAME)) {
                log.trace("Adding environment property " + k + " to configuration");
                _config.put(k, v);
            }
        });

        loadFromProperties(System.getProperties());
    }

    public void loadFromClasspathResource(final String resource) throws IOException {
        log.trace("Loading configuration from resource " + resource);
        final Properties properties = new Properties();
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (null != is) {
            properties.load(is);
            loadFromProperties(properties);
        }
        log.trace("Configuration entries after loading from resource " + resource + ": " + _config);
    }

    public void loadFromProperties(final Properties properties) {
        properties.stringPropertyNames().stream()
                .filter(x -> x.startsWith(APP_NAME))
                .forEach(x -> _config.put(x, properties.getProperty(x)));
    }

}
