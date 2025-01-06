package plugins;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class AdditionTests extends RootTest {

    @Test(groups = {"sum"})
    public void sum() {
        assertEquals(10, Math.addExact(6, 4));
    }

    @Test(groups = {"sum"})
    public void sumLargeNumber() {
        assertEquals(1_000_006, Math.addExact(6, 1_000_000));
    }

}
