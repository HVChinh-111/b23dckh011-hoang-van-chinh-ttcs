package com.blog.demo.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a requested resource (post, topic, series, ...) does not exist.
 * Code examples: POST_NOT_FOUND, TOPIC_NOT_FOUND, SERIES_NOT_FOUND.
 */
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String code, String message) {
        super(code, message, HttpStatus.NOT_FOUND);
    }
}
