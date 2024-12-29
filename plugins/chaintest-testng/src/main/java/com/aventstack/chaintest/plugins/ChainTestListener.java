package com.aventstack.chaintest.plugins;

import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.service.ChainPluginService;
import org.testng.IClassListener;
import org.testng.IExecutionListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ChainTestListener implements IExecutionListener, ISuiteListener, IClassListener, ITestListener {

    private static final String TESTNG = "testng";
    private static final List<Test> _suites = Collections.synchronizedList(new ArrayList<>(1));
    private static final List<Test> _contexts = Collections.synchronizedList(new ArrayList<>(1));
    private static final ChainPluginService _service = new ChainPluginService(TESTNG);

    @Override
    public void onExecutionStart() {
        _service.start();
    }

    @Override
    public void onExecutionFinish() {
        _service.executionFinished();
    }

    @Override
    public void onStart(final ISuite suite) {
        _suites.add(new Test(suite.getName()));
    }

    @Override
    public void onFinish(final ISuite suite) {
        _suites.stream().filter(x -> x.getName().equals(suite.getName()))
                .findAny().ifPresent(x -> {
                    x.complete();
                    _service.afterTest(x, Optional.empty());
                    _service.flush();
                });
    }

    @Override
    public void onBeforeClass(final ITestClass testClass) {
        _suites.stream().filter(x -> x.getName().equals(testClass.getXmlTest().getSuite().getName())).findAny()
                .ifPresent(suite -> {
                    final Test contextTest = new Test(testClass.getName());
                    _contexts.add(contextTest);
                    suite.addChild(contextTest);
                });
    }

    @Override
    public void onAfterClass(final ITestClass testClass) {
        _contexts.stream().filter(x -> x.getName().equals(testClass.getName()))
                .findAny().ifPresent(Test::complete);
    }

    @Override
    public void onStart(final ITestContext context) {
    }

    @Override
    public void onFinish(final ITestContext context) {
    }

    @Override
    public void onTestStart(final ITestResult result) {
        _contexts.stream().filter(x -> x.getName().equals(result.getTestClass().getName()))
                .findAny().ifPresent(x ->
                    x.addChild(new Test(result.getMethod().getMethodName(),
                            Optional.of(result.getTestClass().getName()),
                            List.of(result.getMethod().getGroups()))));
    }

    @Override
    public void onTestSuccess(final ITestResult result) {
        onTestComplete(result);
    }

    private void onTestComplete(final ITestResult result) {
        _contexts.stream()
                .filter(x -> x.getName().equals(result.getTestClass().getName())).findAny()
                .ifPresent(x -> {
                    final Optional<Test> method = x.getChildren().stream()
                            .filter(y -> y.getName().equals(result.getMethod().getMethodName()))
                            .findAny();
                    method.ifPresent(y -> y.complete(result.getThrowable()));
                });
    }

    @Override
    public void onTestFailure(final ITestResult result) {
        onTestComplete(result);
    }

    @Override
    public void onTestSkipped(final ITestResult result) {
        onTestComplete(result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(final ITestResult result) {
    }

    @Override
    public void onTestFailedWithTimeout(final ITestResult result) {
        onTestFailure(result);
    }

}
