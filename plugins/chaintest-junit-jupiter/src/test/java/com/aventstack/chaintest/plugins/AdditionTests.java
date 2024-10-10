package com.aventstack.chaintest.plugins;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

public class AdditionTests extends RootTest {

    @Test
    @Tag("pos")
    public void sum() {
        Assertions.assertEquals(10, Math.addExact(6, 4));
    }

    @Test
    @Tag("pos")
    public void sumLargeNumber() {
        Assertions.assertEquals(1_000_006, Math.addExact(6, 1_000_000));
    }

    @Test
    @Tag("pos")
    public void sumLargeNumbers() {
        Assertions.assertEquals(10_000_000, Math.addExact(9_000_000, 1_000_000));
    }

    @Test
    @Tag("neg")
    public void sumNegative() {
        Assertions.assertEquals(-10, Math.addExact(-6, -4));
    }

    @Test
    @Tag("neg")
    public void sumNegativeLargeNumber() {
        Assertions.assertEquals(-1_000_001, Math.addExact(-1, -1_000_000));
    }

    @Test
    @Tag("neg")
    public void sumNegativeLargeNumbers() {
        Assertions.assertEquals(-10_000_000, Math.addExact(-9_000_000, -1_000_000));
    }

    @Test
    @Tags({@Tag("neg"), @Tag("pos")})
    public void sumOfNegativeAndPositive() {
        Assertions.assertEquals(10, Math.addExact(-6, 16));
    }

}
