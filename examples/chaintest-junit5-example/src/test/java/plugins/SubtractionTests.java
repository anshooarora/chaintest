package plugins;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("subtraction")
public class SubtractionTests extends RootTest {

    @Test
    public void diff() {
        Assertions.assertEquals(2, Math.subtractExact(6, 4));
    }

}
