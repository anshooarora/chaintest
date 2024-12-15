package com.aventstack.chainlp.api.build;

import com.aventstack.chainlp.api.domain.NotFoundException;

public class BuildNotFoundException extends NotFoundException {

    public BuildNotFoundException(final String s) {
        super(s);
    }

}
