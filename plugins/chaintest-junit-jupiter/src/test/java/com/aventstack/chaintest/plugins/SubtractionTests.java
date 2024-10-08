package com.aventstack.chaintest.plugins;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class SubtractionTests extends RootTest {

    @Test
    @Tag("pos")
    public void diffOfPositive() {
        Assertions.assertEquals(10, 14 - 4);
    }

    @Test
    @Tag("neg")
    public void diffOfNegative() {
        Assertions.assertEquals(-10, -16 - -6);
    }

}
