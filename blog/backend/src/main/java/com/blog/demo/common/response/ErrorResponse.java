package com.blog.demo.common.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard error envelope: { "code": "...", "message": "..." }.
 * Optionally carries field-level validation errors.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String code, String message, Map<String, String> errors) {

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, null);
    }

    public static ErrorResponse of(String code, String message, Map<String, String> errors) {
        return new ErrorResponse(code, message, errors);
    }
}
