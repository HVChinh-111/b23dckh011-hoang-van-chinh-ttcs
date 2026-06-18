package com.blog.demo.common.response;

/**
 * Standard success envelope: { "data": ... }
 */
public record ApiResponse<T>(T data) {

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data);
    }
}
