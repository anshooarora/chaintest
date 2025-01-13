package com.aventstack.chaintest.service;

import com.aventstack.chaintest.conf.ConfigurationManager;
import com.aventstack.chaintest.domain.Build;
import com.aventstack.chaintest.domain.Embed;
import com.aventstack.chaintest.domain.SystemInfo;
import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.generator.ChainTestPropertyKeys;
import com.aventstack.chaintest.generator.Generator;
import com.aventstack.chaintest.storage.StorageService;
import com.aventstack.chaintest.storage.StorageServiceFactory;
import com.aventstack.chaintest.util.RegexUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChainPluginService {

    private static final Logger logger = LoggerFactory.getLogger(ChainPluginService.class);
    private static final String GEN_PATTERN = "chaintest.generator.[a-zA-Z]+.enabled";
    private static final String STORAGE_SERVICE = "chaintest.storage.service";
    private static final String STORAGE_SERVICE_ENABLED = STORAGE_SERVICE + ".enabled";
    private static final Queue<Test> _tests = new ConcurrentLinkedQueue<>();
    private static final Map<String, Embed> _embeds = new ConcurrentHashMap<>();
    private static final Map<String, Queue<String>> _logs = new ConcurrentHashMap<>();
    private static final AtomicBoolean START_INVOKED = new AtomicBoolean();
    private static final List<String> SYS_PROPS = List.of(
            "java.version",
            "java.vm.name",
            "java.vm.vendor",
            "java.class.version",
            "java.runtime.name",
            "os.name",
            "os.arch",
            "os.version"
    );

    @Getter
    public static ChainPluginService instance;

    private final Build _build;
    private final String _testRunner;
    private final List<Generator> _generators = new ArrayList<>(3);
    private StorageService _storageService;

    public ChainPluginService(final String testRunner) {
        instance = this;
        _build = new Build(testRunner);
        _build.setSystemInfo(getProps());
        _testRunner = testRunner;
    }

    private List<SystemInfo> getProps() {
        final List<SystemInfo> list = new ArrayList<>();
        for (final String prop : SYS_PROPS) {
            list.add(new SystemInfo(prop, System.getProperty(prop)));
        }
        return list;
    }

    public Build getBuild() {
        return _build;
    }

    public Map<String, Queue<String>> getLogs() {
        return _logs;
    }

    public void register(final Generator generator) {
        _generators.add(generator);
    }

    public void register(final Collection<Generator> generators) {
        _generators.addAll(generators);
    }

    public void start() {
        final Optional<Map<String, String>> config = Optional.ofNullable(ConfigurationManager.getConfig());
        if (!START_INVOKED.getAndSet(true)) {
            config.ifPresent(this::init);
        }
        _generators.stream().filter(x -> !x.started())
                .forEach(x -> x.start(config, _testRunner, _build));
    }

    private void init(final Map<String, String> config) {
        _build.setProjectName(config.get(ChainTestPropertyKeys.PROJECT_NAME));
        register(config);
        startStorageService(config);
    }

    private void register(final Map<String, String> config) {
        final Set<String> generatorNames = new HashSet<>();
        _generators.forEach(x -> generatorNames.add(x.getName()));
        for (final Map.Entry<String, String> entry : config.entrySet()) {
            logger.trace("Reading property: {}", entry.getKey());
            if (entry.getKey().matches(GEN_PATTERN) && !generatorNames.contains(entry.getKey().split("\\.")[2])) {
                final String enabled = entry.getValue();
                if (!Boolean.parseBoolean(enabled)) {
                    logger.debug("Generator {} was not enabled. To enable, set property {}=true in your configuration", entry.getKey(), entry.getKey());
                    continue;
                }
                final String classNameKey = RegexUtil.match("(.*)\\.", entry.getKey()) + "class-name";
                logger.debug("Found configuration entry {}, will use {} to resolve class", entry.getKey(), classNameKey);
                if (!config.containsKey(classNameKey)) {
                    logger.info("{} was true from configuration but the required property {} was not provided", entry.getKey(), classNameKey);
                    continue;
                }
                final String className = config.get(classNameKey);
                try {
                    final Class<?> clazz = Class.forName(className);
                    final Generator gen = (Generator) clazz.getDeclaredConstructor().newInstance();
                    _generators.add(gen);
                } catch (Exception e) {
                    logger.error("Failed to create an instance of {} generator", className, e);
                }
            }
        }
    }

    private void startStorageService(final Map<String, String> config) {
        if (config.containsKey(STORAGE_SERVICE_ENABLED) && Boolean.parseBoolean(config.get(STORAGE_SERVICE_ENABLED))) {
            final String name = config.get(STORAGE_SERVICE);
            final StorageService storageService = StorageServiceFactory.getStorageService(name);
            if (storageService.create(config)) {
                _storageService = storageService;
            }
        }
    }

    public void afterTest(final Test test, final Optional<Throwable> throwable) {
        _tests.add(test);
        _build.updateStats(test);
        _generators.forEach(x -> x.afterTest(test, throwable));
    }

    public void flush() {
        embedRemaining();
        attachLogs();
        _build.complete();
        _generators.forEach(x -> x.flush(_tests));
    }

    public void executionFinished() {
        if (null != _storageService) {
            _storageService.close();
        }
        flush();
        _generators.forEach(Generator::executionFinished);
    }

    public void embed(final String externalId, final byte[] data, final String mimeType) {
        embed(externalId, new Embed(data, mimeType));
    }

    public void embed(final String externalId, final File file, final String mimeType) {
        embed(externalId, new Embed(file, mimeType));
    }

    public void embed(final String externalId, final String base64, final String mimeType) {
        embed(externalId, new Embed(base64, mimeType));
    }

    public void embed(final String externalId, final Embed embed) {
        System.out.println("Setting embed for " + externalId);
        _embeds.put(externalId, embed);
        embed(_tests, externalId, embed);
    }

    public void embed(final Method method, final byte[] data, final String mimeType) {
        embed(getQualifiedName(method), new Embed(data, mimeType));
    }

    public void embed(final Method method, final File file, final String mimeType) {
        embed(getQualifiedName(method), new Embed(file, mimeType));
    }

    public void embed(final Method method, final String base64, final String mimeType) {
        embed(getQualifiedName(method), new Embed(base64, mimeType));
    }

    public void embed(final Method method, final Embed embed) {
        final String externalId = getQualifiedName(method);
        _embeds.put(externalId, embed);
        embed(_tests, externalId, embed);
    }

    public void embed(final Test test, final byte[] data, final String mimeType) {
        embed(test, new Embed(data, mimeType));
    }

    public void embed(final Test test, final File file, final String mimeType) {
        embed(test, new Embed(file, mimeType));
    }

    public void embed(final Test test, final String base64, final String mimeType) {
        embed(test, new Embed(base64, mimeType));
    }

    public void embed(final Test test, final Embed embed) {
        putBlob(test, embed);
        test.addEmbed(embed);
        if (null != test.getExternalId()) {
            _embeds.remove(test.getExternalId());
        }
    }

    public String getQualifiedName(final Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    private void putBlob(final Test test, final Embed embed) {
        if (null != _storageService) {
            _storageService.upload(test, embed);
        }
    }

    private void embed(final Queue<Test> tests, final String externalId, final Embed embed) {
        final Optional<Test> any = testByExternalId(tests, externalId);
        if (any.isPresent()) {
            putBlob(any.get(), embed);
            embed(any.get(), embed);
            _embeds.remove(externalId);
        }
    }

    private Optional<Test> testByExternalId(final Queue<Test> tests, final String externalId) {
        for (Test test : tests) {
            if (externalId.equals(test.getExternalId())) {
                return Optional.of(test);
            }
            final Optional<Test> child = testByExternalId(test.getChildren(), externalId);
            if (child.isPresent()) {
                return child;
            }
        }
        return Optional.empty();
    }

    private void embedRemaining() {
        for (final Map.Entry<String, Embed> entry : _embeds.entrySet()) {
            embed(_tests, entry.getKey(), entry.getValue());
        }
    }

    public void addSystemInfo(final String key, final String value) {
        _build.getSystemInfo().add(new SystemInfo(key, value));
    }

    public void addSystemInfo(final Map<String, String> sysInfo) {
        sysInfo.forEach(this::addSystemInfo);
    }

    public void log(final String externalId, final String message) {
        testByExternalId(_tests, externalId)
            .ifPresentOrElse(
                    test -> test.addLog(message),
                    () -> _logs.computeIfAbsent(externalId,
                            k -> new ConcurrentLinkedQueue<>()).add(message));
    }

    public void log(final Method method, final String message) {
        log(getQualifiedName(method), message);
    }

    private void attachLog(final Test test, final String externalId) {
        final Queue<String> logs = _logs.get(externalId);
        if (null != logs) {
            logs.forEach(test::addLog);
            _logs.remove(externalId);
        }
    }

    private void attachLogs() {
        for (final Map.Entry<String, Queue<String>> entry : _logs.entrySet()) {
            final Optional<Test> any = testByExternalId(_tests, entry.getKey());
            any.ifPresent(test -> attachLog(test, entry.getKey()));
        }
    }

    private List<Test> flattenTests() {
        return _tests.stream()
                .flatMap(test -> flattenTests(test).stream())
                .collect(Collectors.toList());
    }

    private List<Test> flattenTests(Test test) {
        return Stream.concat(
                Stream.of(test),
                test.getChildren().stream().flatMap(child -> flattenTests(child).stream())
        ).collect(Collectors.toList());
    }

}
