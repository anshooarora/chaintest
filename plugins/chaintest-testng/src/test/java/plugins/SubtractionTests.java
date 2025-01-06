package plugins;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SubtractionTests extends RootTest {

    @Test(groups = {"diff"})
    public void diff() {
        assertEquals(2, Math.subtractExact(6, 4));
    }

    @Test(groups = {"diff"})
    public void diffLargeNumber() {
        assertEquals(1_000_000, Math.subtractExact(1_000_006, 6));
    }

}
