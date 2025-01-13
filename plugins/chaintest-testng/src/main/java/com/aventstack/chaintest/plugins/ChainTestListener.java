package com.aventstack.chaintest.plugins;

import com.aventstack.chaintest.domain.Embed;
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

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChainTestListener implements
    IExecutionListener, ISuiteListener, IClassListener, ITestListener, IInvokedMethodListener {

    private static final String TESTNG = "testng";
    private static final Map<String, Test> _suites = new ConcurrentHashMap<>();
    private static final Map<String, Test> _classes = new ConcurrentHashMap<>();
    private static final Map<String, Test> _methods = new ConcurrentHashMap<>();
    private static final ChainPluginService _service = new ChainPluginService(TESTNG);
    private static final ThreadLocal<Test> _currentTest = new ThreadLocal<>();
    private static final ThreadLocal<Queue<String>> _logs = ThreadLocal.withInitial(ConcurrentLinkedQueue::new);
    private static final ThreadLocal<Boolean> _allowLog = ThreadLocal.withInitial(() -> false);

    public static void log(final String log) {
        if (null != _allowLog.get() && _allowLog.get()) {
            _logs.get().add(log);
        }
    }

    public static void embed(final byte[] data, final String mimeType) {
        if (null != _allowLog.get() && _allowLog.get() && _currentTest.get() != null) {
            ChainPluginService.getInstance().embed(_currentTest.get(), new Embed(data, mimeType));
        }
    }

    public static void embed(final File file, final String mimeType) {
        if (null != _allowLog.get() && _allowLog.get() && _currentTest.get() != null) {
            ChainPluginService.getInstance().embed(_currentTest.get(), new Embed(file, mimeType));
        }
    }

    public static void embed(final String base64, final String mimeType) {
        if (null != _allowLog.get() && _allowLog.get() && _currentTest.get() != null) {
            ChainPluginService.getInstance().embed(_currentTest.get(), new Embed(base64, mimeType));
        }
    }

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
        if (method.isConfigurationMethod() && method.getTestMethod().isBeforeMethodConfiguration()) {
            _allowLog.set(true);
        }
        if (method.isTestMethod()) {
            final Test testMethod = new Test(result.getMethod().getMethodName(),
                    Optional.of(result.getTestClass().getName()),
                    List.of(result.getMethod().getGroups()));
            testMethod.setExternalId(result.getMethod().getQualifiedName() + "_" + result.id());
            while (!_logs.get().isEmpty()) {
                testMethod.addLog(_logs.get().poll());
            }
            _classes.get(result.getTestClass().getName()).addChild(testMethod);
            _methods.put(result.getMethod().getQualifiedName(), testMethod);
            _currentTest.set(testMethod);

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

    @Override
    public void afterInvocation(final IInvokedMethod method, final ITestResult result, ITestContext ctx) {
        if (method.isConfigurationMethod() && method.getTestMethod().isAfterMethodConfiguration()) {
            final Test testMethod = _currentTest.get();
            if (null != testMethod) {
                while (!_logs.get().isEmpty()) {
                    testMethod.addLog(_logs.get().poll());
                }
            }
            _allowLog.set(false);
        }
    }

    @Override
    public void onTestSuccess(final ITestResult result) {
        onTestComplete(result);
    }

    private void onTestComplete(final ITestResult result) {
        if (_methods.containsKey(result.getMethod().getQualifiedName())) {
            final Test completingMethod = _currentTest.get();
            completingMethod.complete(result.getThrowable());
            while (!_logs.get().isEmpty()) {
                completingMethod.addLog(_logs.get().poll());
            }
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
