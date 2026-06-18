package com.blog.demo.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a uniqueness rule is violated (duplicate slug / name).
 * Codes: DUPLICATE_SLUG, DUPLICATE_TOPIC, DUPLICATE_SERIES.
 */
public class DuplicateResourceException extends ApiException {

    public DuplicateResourceException(String code, String message) {
        super(code, message, HttpStatus.CONFLICT);
    }
}
