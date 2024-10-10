package com.aventstack.chaintest.plugins;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

public class IntentionalFailureTests {

    @Test
    @Tag("failure")
    @EnabledIfSystemProperty(named = "chaintest.mock", matches = "true")
    public void intentionalFail() {
        Assertions.assertEquals(10, Math.addExact(6, 16));
    }

}
