package com.aventstack.chainlp.conf;

import com.aventstack.chainlp.api.domain.NotFoundException;
import com.aventstack.chainlp.api.domain.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

@Slf4j
@ControllerAdvice(annotations = RestController.class)
public class ExceptionHandlerAdvice {

    @ExceptionHandler({
            NotFoundException.class
    })
    public final ResponseEntity<ErrorResponse> handleException(final Exception ex, final WebRequest request) {
        log.error("Handling exception: {}", ex.getClass().getSimpleName());
        final HttpHeaders headers = new HttpHeaders();
        final HttpStatus status = HttpStatus.NOT_FOUND;
        request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        final ErrorResponse error = new ErrorResponse(ExceptionUtils.getMessage(ex), status);
        error.setStacktrace(ExceptionUtils.getStackTrace(ex));
        return new ResponseEntity<>(error, headers, status);
    }

}