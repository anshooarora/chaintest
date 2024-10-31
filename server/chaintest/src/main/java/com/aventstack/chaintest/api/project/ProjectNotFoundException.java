package com.aventstack.chaintest.api.project;

import com.aventstack.chaintest.api.domain.exception.BaseNotFoundException;

public class ProjectNotFoundException extends BaseNotFoundException {

    public ProjectNotFoundException(final String s) {
        super(s);
    }

}
