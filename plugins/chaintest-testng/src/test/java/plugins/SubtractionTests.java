package plugins;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SubtractionTests extends RootTest {

    @Test(groups = {"pos"})
    public void diff() {
        assertEquals(2, Math.subtractExact(6, 4));
    }

    @Test(groups = {"pos"})
    public void diffLargeNumber() {
        assertEquals(1_000_000, Math.subtractExact(1_000_006, 6));
    }

    @Test(groups = {"pos"})
    public void diffLargeNumbers() {
        assertEquals(8_000_000, Math.subtractExact(9_000_000, 1_000_000));
    }

    @Test(groups = {"neg"})
    public void diffNegative() {
        assertEquals(-2, Math.subtractExact(-6, -4));
    }

    @Test(groups = {"neg"})
    public void diffNegativeLargeNumber() {
        assertEquals(999_999, Math.subtractExact(-1, -1_000_000));
    }

    @Test(groups = {"neg"})
    public void diffNegativeLargeNumbers() {
        assertEquals(-8_000_000, Math.subtractExact(-9_000_000, -1_000_000));
    }

    @Test(groups = {"pos", "neg"})
    public void diffOfNegativeAndPositive() {
        assertEquals(-22, Math.subtractExact(-6, 16));
    }

}
