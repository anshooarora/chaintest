package com.aventstack.chainlp.api.test;

import com.aventstack.chainlp.api.domain.NotFoundException;

public class TestNotFoundException extends NotFoundException {

    public TestNotFoundException(final String s) {
        super(s);
    }

}
