package plugins;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class AdditionTests extends RootTest {

    @Test
    public void sum() {
        assertEquals(10, Math.addExact(6, 4));
    }

    @Test(groups = {"pos"})
    public void sumLargeNumber() {
        assertEquals(1_000_006, Math.addExact(6, 1_000_000));
    }

    @Test(groups = {"pos"})
    public void sumLargeNumbers() {
        assertEquals(10_000_000, Math.addExact(9_000_000, 1_000_000));
    }

    @Test(groups = {"neg"})
    public void sumNegative() {
        assertEquals(-10, Math.addExact(-6, -4));
    }

    @Test(groups = {"neg"})
    public void sumNegativeLargeNumbers() {
        assertEquals(-10_000_000, Math.addExact(-9_000_000, -1_000_000));
    }

    @Test(groups = {"neg"})
    public void sumNegativeLargeNumber() {
        assertEquals(-1_000_001, Math.addExact(-1, -1_000_000));
    }

    @Test(groups = {"pos", "neg"})
    public void sumOfNegativeAndPositive() {
        assertEquals(10, Math.addExact(-6, 16));
    }

}
