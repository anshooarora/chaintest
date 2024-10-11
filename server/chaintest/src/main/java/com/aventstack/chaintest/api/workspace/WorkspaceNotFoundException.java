package com.aventstack.chaintest.api.workspace;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WorkspaceNotFoundException extends RuntimeException {

    public WorkspaceNotFoundException(final String s) {
        super(s);
    }

}
