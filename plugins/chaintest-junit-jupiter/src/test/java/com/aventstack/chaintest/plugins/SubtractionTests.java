package com.aventstack.chaintest.plugins;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

public class SubtractionTests extends RootTest {

    @Test
    @Tag("pos")
    public void diff() {
        Assertions.assertEquals(2, Math.subtractExact(6, 4));
    }

    @Test
    @Tag("pos")
    public void diffLargeNumber() {
        Assertions.assertEquals(1_000_000, Math.subtractExact(1_000_006, 6));
    }

    @Test
    @Tag("pos")
    public void diffLargeNumbers() {
        Assertions.assertEquals(8_000_000, Math.subtractExact(9_000_000, 1_000_000));
    }

    @Test
    @Tag("neg")
    public void diffNegative() {
        Assertions.assertEquals(-2, Math.subtractExact(-6, -4));
    }

    @Test
    @Tag("neg")
    public void diffNegativeLargeNumber() {
        Assertions.assertEquals(999_999, Math.subtractExact(-1, -1_000_000));
    }

    @Test
    @Tag("neg")
    public void diffNegativeLargeNumbers() {
        Assertions.assertEquals(-8_000_000, Math.subtractExact(-9_000_000, -1_000_000));
    }

    @Test
    @Tags({@Tag("neg"), @Tag("pos")})
    public void diffOfNegativeAndPositive() {
        Assertions.assertEquals(-22, Math.subtractExact(-6, 16));
    }

}
