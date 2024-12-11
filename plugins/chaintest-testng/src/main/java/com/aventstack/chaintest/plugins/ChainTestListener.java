package com.aventstack.chaintest.plugins;

import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.service.ChainPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IExecutionListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ChainTestListener implements IExecutionListener, ISuiteListener, ITestListener {

    private static final Logger log = LoggerFactory.getLogger(ChainTestListener.class);
    private static final String TESTNG = "testng";
    private static final List<Test> _suites = Collections.synchronizedList(new ArrayList<>(1));
    private static final List<Test> _contexts = Collections.synchronizedList(new ArrayList<>(1));
    private static final ChainPluginService _service = new ChainPluginService(TESTNG);

    @Override
    public void onExecutionStart() {
        System.out.println("onExecutionStart");
        _service.start();
    }

    @Override
    public void onExecutionFinish() {
        System.out.println("onExecutionFinish");
        _service.executionFinished();
    }

    @Override
    public void onStart(final ISuite suite) {
        System.out.println("Suite started: " + suite.getName());
        _suites.add(new Test(suite.getName()));
    }

    @Override
    public void onFinish(final ISuite suite) {
        System.out.println("Suite finished: " + suite.getName());
        _suites.stream().filter(x -> x.getName().equals(suite.getName()))
                .findAny().ifPresent(x -> {
                    x.complete();
                    _service.afterTest(x, Optional.empty());
                    _service.flush();
                });
    }

    @Override
    public void onStart(final ITestContext context) {
        System.out.println("Test context started: " + context.getName());
        _suites.stream().filter(x -> x.getName().equals(context.getSuite().getName())).findAny()
                .ifPresent(suite -> {
                    final Test contextTest = new Test(context.getName());
                    _contexts.add(contextTest);
                    suite.addChild(contextTest);
                });
    }

    @Override
    public void onFinish(final ITestContext context) {
        System.out.println("Test context finished: " + context.getName());
        _contexts.stream().filter(x -> x.getName().equals(context.getName()))
                .findAny().ifPresent(Test::complete);
    }

    @Override
    public void onTestStart(final ITestResult result) {
        System.out.println("Test started: " + result.getName());
        _contexts.stream().filter(x -> x.getName().equals(result.getTestContext().getName()))
                .findAny().ifPresent(x ->
                    x.addChild(new Test(result.getMethod().getMethodName(),
                            Optional.of(result.getTestClass().getName()),
                            List.of(result.getMethod().getGroups()))));
    }

    @Override
    public void onTestSuccess(final ITestResult result) {
        System.out.println("Test passed: " + result.getName());
        onTestComplete(result);
    }

    private void onTestComplete(final ITestResult result) {
        _contexts.stream()
                .filter(x -> x.getName().equals(result.getTestContext().getName())).findAny()
                .ifPresent(x -> {
                    final Optional<Test> method = x.getChildren().stream()
                            .filter(y -> y.getName().equals(result.getMethod().getMethodName()))
                            .findAny();
                    method.ifPresent(y -> y.complete(result.getThrowable()));
                });
    }

    @Override
    public void onTestFailure(final ITestResult result) {
        System.out.println("Test failed: " + result.getName());
        onTestComplete(result);
    }

    @Override
    public void onTestSkipped(final ITestResult result) {
        System.out.println("Test skipped: " + result.getName());
        onTestComplete(result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(final ITestResult result) {
        System.out.println("Test failed but within success percentage: " + result.getName());
    }

    @Override
    public void onTestFailedWithTimeout(final ITestResult result) {
        onTestFailure(result);
    }

}
