package com.aventstack.chaintest.conf;

import com.aventstack.chaintest.util.RegexUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class YamlConfig {

    private static final String KEY_PATTERN = "\\$\\{([^}]+)}";

    private Map<String, Object> _yaml;

    private YamlConfig() { }

    public static YamlConfig load(final InputStream is) {
        final Yaml yaml = new Yaml();
        final YamlConfig instance = new YamlConfig();
        instance._yaml = yaml.load(is);
        return instance;
    }

    public static YamlConfig load(final String path) throws FileNotFoundException {
        return load(new FileInputStream(path));
    }

    public Optional<String> getString(final String key) {
        final Optional<Object> obj = getObject(_yaml, key);
        if (obj.isPresent() && !(obj.get() instanceof Collection)) {
            return Optional.of(obj.get().toString());
        }
        return Optional.empty();
    }

    public Optional<Integer> getInt(final String key) {
        final Optional<Object> val = getObject(key);
        if (val.isPresent() && val.get() instanceof Integer) {
            return Optional.of((Integer) val.get());
        }
        return Optional.empty();
    }

    public Optional<Collection<?>> getCollection(final String key) {
        final Optional<Object> obj = getObject(_yaml, key);
        if (obj.isPresent() && obj.get() instanceof Collection) {
            return Optional.of((Collection<?>) obj.get());
        }
        return Optional.empty();
    }

    public Optional<Object> getObject(final String key) {
        return getObject(_yaml, key);
    }

    private Optional<Object> getObject(Object content, final String key) {
        final String match = RegexUtil.match(KEY_PATTERN, key);
        final String[] parts = (null == match || match.isEmpty() ? key : match)
                .split("\\.");
        for (final String part : parts) {
            if (content instanceof Map) {
                content = ((Map<?, ?>) content).get(part);
            }
        }
        if (null != content && content.toString().matches(KEY_PATTERN)) {
            final String subKey = RegexUtil.match(KEY_PATTERN, content.toString());
            content = getObject(_yaml, subKey);
        }
        return Optional.of(content);
    }

}
