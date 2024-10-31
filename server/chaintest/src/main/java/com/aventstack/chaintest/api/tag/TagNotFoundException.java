package com.aventstack.chaintest.api.tag;

import com.aventstack.chaintest.api.domain.exception.BaseNotFoundException;

public class TagNotFoundException extends BaseNotFoundException {

    public TagNotFoundException(final String s) {
        super(s);
    }

}
