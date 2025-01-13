package com.aventstack.chaintest.plugins;

import com.aventstack.chaintest.domain.Test;
import com.aventstack.chaintest.service.ChainPluginService;
import org.testng.IClassListener;
import org.testng.IExecutionListener;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ChainTestListener implements
        IExecutionListener, ISuiteListener, IClassListener, ITestListener, IInvokedMethodListener {

    private static final String TESTNG = "testng";
    private static final Map<String, Test> _suites = new ConcurrentHashMap<>();
    private static final Map<String, Test> _classes = new ConcurrentHashMap<>();
    private static final Map<String, Test> _methods = new ConcurrentHashMap<>();
    private static final ChainPluginService _service = new ChainPluginService(TESTNG);

    private static final Map<Long, String> _externalIds = new ConcurrentHashMap<>();

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
    public void beforeInvocation(final IInvokedMethod method, final ITestResult result) {
        if (method.isTestMethod()) {
            final Test testMethod = new Test(result.getMethod().getMethodName(),
                    Optional.of(result.getTestClass().getName()),
                    List.of(result.getMethod().getGroups()));
            testMethod.setExternalId(result.getMethod().getQualifiedName() + "_" + result.id());
            _externalIds.put(Thread.currentThread().getId(), testMethod.getExternalId());
            _classes.get(result.getTestClass().getName()).addChild(testMethod);
            _methods.put(result.getMethod().getQualifiedName(), testMethod);

            if (null != result.getParameters() && result.getParameters().length > 0) {
                final String params = String.join(", ", Arrays.stream(result.getParameters())
                        .filter(p -> null != p && !(p instanceof Method) && !(p instanceof ITestContext) && !(p instanceof ITestResult))
                        .map(Object::toString)
                        .toArray(String[]::new));
                if (!params.isEmpty()) {
                    testMethod.setDescription("[" + params + "]");
                }
            }
        }
    }

    public static String getExternalId(final long threadId) {
        return _externalIds.get(threadId);
    }

    @Override
    public void onTestSuccess(final ITestResult result) {
        onTestComplete(result);
    }

    private void onTestComplete(final ITestResult result) {
        if (_methods.containsKey(result.getMethod().getQualifiedName())) {
            _methods.get(result.getMethod().getQualifiedName()).complete(result.getThrowable());
        }
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
    public void onTestFailedWithTimeout(final ITestResult result) {
        onTestFailure(result);
    }

}
