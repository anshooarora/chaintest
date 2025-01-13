package com.aventstack;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;

public class Hooks {

    @After
    public void after(final Scenario scenario) {
        scenario.log("This is a log message from the Hooks class");
    }

}
