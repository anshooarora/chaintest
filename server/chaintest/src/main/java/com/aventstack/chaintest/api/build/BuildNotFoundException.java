package com.aventstack.chaintest.api.build;

import com.aventstack.chaintest.api.domain.exception.BaseNotFoundException;

public class BuildNotFoundException extends BaseNotFoundException {

    public BuildNotFoundException(final String s) {
        super(s);
    }

}
