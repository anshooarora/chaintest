package plugins;

import com.aventstack.chaintest.plugins.ChainTestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DataProviderParallelTest extends RootTest{

    @Test
    public void log() {
        ChainTestListener.log("log example");
    }

    @Test(dataProvider = "additionDataProvider")
    public void sum(int a, int b, int expectedSum) {
        ChainTestListener.log(a + " + " + b + " = " + expectedSum);
        assertEquals(expectedSum, Math.addExact(a, b));
    }

    @BeforeMethod
    public void beforeMethod(ITestResult result) {
        ChainTestListener.log("starting test " + result.getMethod().getQualifiedName() + Thread.currentThread().getName());
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        ChainTestListener.log("ending test " + result.getMethod().getQualifiedName() + Thread.currentThread().getName());
    }

    @DataProvider(name = "additionDataProvider", parallel = true)
    public Object[][] additionDataProvider(ITestNGMethod method) {
        Object[][] testData = new Object[20][3];
        for (int i = 0; i < 20; i++) {
            int b = 100 - i;
            int sum = i + b;
            testData[i][0] = i;
            testData[i][1] = b;
            testData[i][2] = sum;
        }
        return testData;
    }

}
