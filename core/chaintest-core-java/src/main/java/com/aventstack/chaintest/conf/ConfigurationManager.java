package com.aventstack.chaintest.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ConfigurationManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationManager.class);
    private static final ConfigurationManager INSTANCE = new ConfigurationManager();

    private static Configuration _conf;

    public void load() {
        if (_conf == null) {
            try {
                final Configuration conf = new Configuration();
                conf.load();
                _conf = conf;
            } catch (final IOException e) {
                log.error("Runtime exception was raised while loading configuration", e);
            }
        }
    }

    public static synchronized Optional<Configuration> getConfiguration() {
        if (_conf == null) {
            INSTANCE.load();
        }
        return Optional.of(_conf);
    }

    public static Map<String, String> getConfig() {
        return getConfiguration().map(Configuration::getConfig).orElse(null);
    }

    public static boolean containsKey(final String key) {
        return getConfiguration().map(config -> config.getConfig().containsKey(key)).orElse(false);
    }

    public static String getValue(final String key) {
        return getConfiguration().map(config -> config.getConfig().get(key)).orElse(null);
    }

    public static int parseConfig(final String value, final int defaultValue) {
        return (value != null && value.matches("\\d+")) ? Integer.parseInt(value) : defaultValue;
    }

    public static long parseConfig(final String value, final long defaultValue) {
        return (value != null && value.matches("\\d+")) ? Long.parseLong(value) : defaultValue;
    }

}
