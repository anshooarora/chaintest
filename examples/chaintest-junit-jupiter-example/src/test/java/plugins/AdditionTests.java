package plugins;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("addition")
public class AdditionTests extends RootTest {

    @Test
    public void sum() {
        Assertions.assertEquals(10, Math.addExact(6, 4));
    }

}
