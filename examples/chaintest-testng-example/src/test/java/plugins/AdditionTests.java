package plugins;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = {"addition"})
public class AdditionTests extends RootTest {

    @Test
    public void sum() {
        assertEquals(10, Math.addExact(6, 4));
    }

}
