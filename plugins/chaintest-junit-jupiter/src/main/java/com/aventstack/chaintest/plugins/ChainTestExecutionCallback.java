package com.aventstack.chaintest.plugins;

import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.generator.ChainTestEmailGenerator;
import com.aventstack.chaintest.generator.ChainTestSimpleGenerator;
import com.aventstack.chaintest.http.ChainTestApiClient;
import com.aventstack.chaintest.service.ChainPluginService;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class ChainTestExecutionCallback
        implements BeforeAllCallback, AfterAllCallback, AfterTestExecutionCallback {

    private static final Logger log = LoggerFactory.getLogger(ChainTestExecutionCallback.class);
    private static final String JUNIT_JUPITER = "junit-jupiter";
    private static final AtomicBoolean CALLBACK_INVOKED = new AtomicBoolean();

    private static ChainPluginService _service;

    @Override
    public void beforeAll(final ExtensionContext extensionContext) throws Exception {
        if (CALLBACK_INVOKED.getAndSet(true)) {
            return;
        }
        log.trace("Creating instance of {}", ChainTestApiClient.class);
        _service = new ChainPluginService(JUNIT_JUPITER);
        _service.register(new ChainTestSimpleGenerator());
        _service.register(new ChainTestEmailGenerator());
        _service.start();
    }

    @Override
    public void afterTestExecution(final ExtensionContext context) throws Exception {
        final Test test = new Test(context.getDisplayName(),
                context.getTestClass().map(Class::getName),
                context.getTags());
        _service.afterTest(test, context.getExecutionException());
        log.trace("Ended test {} with status {}", test.getName(), test.getResult());
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) {
        log.trace("Executing afterAll hook");
        _service.flush();
    }

}
