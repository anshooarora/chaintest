package com.aventstack.chaintest.plugins;

import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.service.ChainPluginService;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChainTestExecutionCallback
        implements BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger log = LoggerFactory.getLogger(ChainTestExecutionCallback.class);
    private static final Map<String, Test> TESTS = new ConcurrentHashMap<>();
    private static final String JUNIT_JUPITER = "junit-jupiter";
    private static final AtomicBoolean CALLBACK_INVOKED = new AtomicBoolean();

    private static ChainPluginService _service;

    @Override
    public void beforeAll(final ExtensionContext extensionContext) {
        if (CALLBACK_INVOKED.getAndSet(true)) {
            return;
        }
        _service = new ChainPluginService(JUNIT_JUPITER);
        _service.start();
    }

    @Override
    public void beforeTestExecution(final ExtensionContext context) {
        context.getTestClass().ifPresent(testclass -> {
            final String className = testclass.getName();
            TESTS.computeIfAbsent(className, key -> {
                final Test test = new Test(className, Optional.of(className), context.getTags());
                test.setExternalId(className);
                return test;
            });
            final Test test = new Test(context.getDisplayName(), Optional.of(className), context.getTags());
            test.setExternalId(getExternalId(context));
            TESTS.get(className).addChild(test);
        });
    }

    @Override
    public void afterTestExecution(final ExtensionContext context) {
        context.getTestClass().ifPresent(testclass -> {
            final Test test = TESTS.get(testclass.getName())
                    .getChildren().stream()
                    .filter(t -> t.getExternalId().equals(getExternalId(context))).findAny()
                    .orElseThrow(() -> new IllegalStateException("Test not found"));
            test.complete(context.getExecutionException());
            log.trace("Ended test {} with status {}", test.getName(), test.getResult());
        });
    }

    private String getExternalId(final ExtensionContext context) {
        if (context.getTestMethod().isPresent()) {
            return _service.getQualifiedName(context.getTestMethod().get());
        } else {
            if (context.getTestClass().isPresent()) {
                return context.getTestClass().get().getName() + "." + context.getDisplayName();
            } else {
                return context.getDisplayName();
            }
        }
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) {
        log.trace("Executing afterAll hook");
        extensionContext.getTestClass().ifPresent(ctx -> {
            final Test test = TESTS.get(ctx.getName());
            if (null != test) {
                _service.afterTest(test, Optional.empty());
                _service.flush();
            }
        });
    }

}
