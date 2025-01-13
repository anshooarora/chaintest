package plugins;

import com.aventstack.chaintest.plugins.ChainTestListenerService;
import org.testng.ITestNGMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DataProviderParallelTest extends RootTest{

    @Test
    public void log() {
        ChainTestListenerService.log("log example");
    }

    @Test(dataProvider = "additionDataProvider")
    public void sum(int a, int b, int expectedSum) {
        ChainTestListenerService.log(a + " + " + b + " = " + expectedSum);
        assertEquals(expectedSum, Math.addExact(a, b));
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
