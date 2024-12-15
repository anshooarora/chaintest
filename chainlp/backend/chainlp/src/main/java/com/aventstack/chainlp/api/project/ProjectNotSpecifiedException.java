package com.aventstack.chainlp.api.project;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProjectNotSpecifiedException extends IllegalArgumentException {

    public ProjectNotSpecifiedException(final String message) {
        super(message);
    }

}
