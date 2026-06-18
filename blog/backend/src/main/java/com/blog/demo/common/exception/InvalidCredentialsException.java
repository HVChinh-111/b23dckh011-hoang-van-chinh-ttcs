package com.blog.demo.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Authentication failure. Message is intentionally generic (BR05.2) so it does
 * not reveal whether the email or the password was wrong.
 */
public class InvalidCredentialsException extends ApiException {

    public InvalidCredentialsException(String message) {
        super("INVALID_CREDENTIALS", message, HttpStatus.UNAUTHORIZED);
    }
}
