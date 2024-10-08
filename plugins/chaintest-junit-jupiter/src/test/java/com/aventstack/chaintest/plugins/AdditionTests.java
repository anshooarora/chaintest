package com.aventstack.chaintest.plugins;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestExecutionCallback.class)
public class AdditionTests {

    @Test
    @Tag("pos")
    public void sumOfPositive() {
        Assertions.assertEquals(10, 6 + 4);
    }

    @Test
    @Tag("neg")
    public void sumOfNegative() {
        Assertions.assertEquals(-10, -6 + -4);
    }

    @Test
    @Tags({@Tag("neg"), @Tag("pos")})
    public void sumOfNegativeAndPositive() {
        Assertions.assertEquals(10, -6 + 16);
    }

    @Test
    @Tag("willFail")
    public void intentionalFail() {
        Assertions.assertEquals(10, 6 + 16);
    }

}
