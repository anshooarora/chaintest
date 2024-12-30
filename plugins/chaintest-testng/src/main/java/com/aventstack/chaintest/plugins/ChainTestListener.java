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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ChainTestListener implements IExecutionListener, ISuiteListener, IClassListener, ITestListener {

    private static final String TESTNG = "testng";
    private static final Map<String, Test> _suites = new ConcurrentHashMap<>();
    private static final Map<String, Test> _classes = new ConcurrentHashMap<>();
    private static final Map<String, Test> _methods = new ConcurrentHashMap<>();
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
        _suites.put(suite.getName(), new Test(suite.getName()));
    }

    @Override
    public void onFinish(final ISuite suite) {
        final Test suiteTest = _suites.get(suite.getName());
        suiteTest.complete();
        _service.afterTest(suiteTest, Optional.empty());
        _service.flush();
    }

    @Override
    public void onBeforeClass(final ITestClass testClass) {
        final Test testClazz = new Test(testClass.getName());
        _classes.put(testClass.getName(), testClazz);
        _suites.get(testClass.getXmlTest().getSuite().getName()).addChild(testClazz);
    }

    @Override
    public void onAfterClass(final ITestClass testClass) {
        _classes.get(testClass.getName()).complete();
    }

    @Override
    public void onStart(final ITestContext context) {
    }

    @Override
    public void onFinish(final ITestContext context) {
    }

    @Override
    public void onTestStart(final ITestResult result) {
        final Test method = new Test(result.getMethod().getMethodName(),
                Optional.of(result.getTestClass().getName()),
                List.of(result.getMethod().getGroups()));
        method.setExternalId(result.getMethod().getQualifiedName());
        _classes.get(result.getTestClass().getName()).addChild(method);
        _methods.put(result.getMethod().getQualifiedName(), method);
    }

    @Override
    public void onTestSuccess(final ITestResult result) {
        onTestComplete(result);
    }

    private void onTestComplete(final ITestResult result) {
        _methods.get(result.getMethod().getQualifiedName()).complete(result.getThrowable());
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
