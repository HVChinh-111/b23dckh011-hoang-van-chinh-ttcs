package com.blog.demo.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Business-level validation failure (e.g. blank search keyword, invalid slug
 * format, invalid contact URL).
 */
public class ValidationException extends ApiException {

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message, HttpStatus.BAD_REQUEST);
    }

    public ValidationException(String code, String message) {
        super(code, message, HttpStatus.BAD_REQUEST);
    }
}
