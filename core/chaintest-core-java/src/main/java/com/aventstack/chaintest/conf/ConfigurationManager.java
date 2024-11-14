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

    public synchronized static Optional<Configuration> getConfiguration() {
        if (_conf == null) {
            INSTANCE.load();
        }
        return Optional.of(_conf);
    }

    public static Map<String, String> getConfig() {
        if (getConfiguration().isPresent()) {
            return getConfiguration().get().getConfig();
        }
        return null;
    }

    public static boolean containsKey(final String key) {
        if (getConfiguration().isPresent()) {
            return getConfiguration().get().getConfig().containsKey(key);
        }
        return false;
    }

    public static String getValue(final String key) {
        if (getConfiguration().isPresent()) {
            return getConfiguration().get().getConfig().get(key);
        }
        return null;
    }

}
