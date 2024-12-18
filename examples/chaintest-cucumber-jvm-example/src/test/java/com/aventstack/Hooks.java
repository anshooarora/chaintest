package com.aventstack;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class Hooks {

    @After
    public void beforeHook(Scenario s) throws IOException {
        //s.log("Hello");
        final String b64 = Files.readString(new File("/Users/anshooarora/workspace/github/orgs/anshooarora/chaintest/core/chaintest-core-java/src/main/resources/b64.txt").toPath());
        byte[] bytesEncoded = Base64.getDecoder().decode(b64.getBytes());
        //s.attach(bytesEncoded, "image/png", "img");
    }

}
