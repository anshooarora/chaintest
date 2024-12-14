package com.aventstack.chainserv.api.project;

import com.aventstack.chainserv.api.domain.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProjectNotFoundException extends NotFoundException {

    public ProjectNotFoundException(final String s) {
        super(s);
    }

}
