package com.aventstack.chaintest.plugins;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

public class AdditionTests extends RootTest {

    @Test
    public void sum() {
        Assertions.assertEquals(10, Math.addExact(6, 4));
    }

    @Test
    @Tag("pos")
    public void sumLargeNumber() {
        Assertions.assertEquals(1_000_006, Math.addExact(6, 1_000_000));
    }

}
