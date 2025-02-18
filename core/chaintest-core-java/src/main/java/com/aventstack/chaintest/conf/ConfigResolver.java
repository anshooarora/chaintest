package com.aventstack.chaintest.conf;

public class ConfigResolver {

    public static String get(final String prop) {
        if (System.getProperty(prop) != null) {
            return System.getProperty(prop);
        }
        if (System.getenv(prop) != null) {
            return System.getenv(prop);
        }
        return ConfigurationManager.getValue(prop);
    }

}
