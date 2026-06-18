package com.blog.demo.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a file upload is invalid (bad type/size) or storage I/O fails.
 */
public class FileStorageException extends ApiException {

    public FileStorageException(String message) {
        super("FILE_STORAGE_ERROR", message, HttpStatus.BAD_REQUEST);
    }

    public FileStorageException(String code, String message, HttpStatus status) {
        super(code, message, status);
    }
}
