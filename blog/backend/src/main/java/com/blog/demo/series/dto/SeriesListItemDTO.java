package com.blog.demo.series.dto;

/** Series listing with its number of posts (UC07). */
public record SeriesListItemDTO(
        String id,
        String name,
        String slug,
        String description,
        long postCount) {
}
