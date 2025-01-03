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

}
