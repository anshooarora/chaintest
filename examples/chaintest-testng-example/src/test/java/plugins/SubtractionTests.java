package plugins;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = {"subtraction"})
public class SubtractionTests extends RootTest {

    @Test
    public void diff() {
        assertEquals(2, Math.subtractExact(6, 4));
    }

}
