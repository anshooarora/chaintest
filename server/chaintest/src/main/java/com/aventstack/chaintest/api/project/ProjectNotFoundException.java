package com.aventstack.chaintest.api.project;

import com.aventstack.chaintest.api.domain.exception.BaseNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProjectNotFoundException extends BaseNotFoundException {

    public ProjectNotFoundException(final String s) {
        super(s);
    }

}
