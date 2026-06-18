package com.blog.demo.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Base for all domain exceptions. Carries a stable error {@code code} and the
 * HTTP status the {@code GlobalExceptionHandler} should map it to.
 */
@Getter
public abstract class ApiException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    protected ApiException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}
