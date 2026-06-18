package com.blog.demo.common.response;

import org.springframework.data.domain.Page;

/**
 * Pagination metadata returned alongside paged lists.
 * currentPage is 1-based to match the frontend.
 */
public record PageInfoDTO(
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious) {

    public static PageInfoDTO from(Page<?> page) {
        return new PageInfoDTO(
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious());
    }
}
