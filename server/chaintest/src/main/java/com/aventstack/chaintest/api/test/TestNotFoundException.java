package com.aventstack.chaintest.api.test;

import com.aventstack.chaintest.api.domain.exception.BaseNotFoundException;

public class TestNotFoundException extends BaseNotFoundException {

    public TestNotFoundException(final String s) {
        super(s);
    }

}
