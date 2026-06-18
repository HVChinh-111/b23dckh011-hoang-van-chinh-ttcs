package com.blog.demo.series.dto;

/** A post within a series, in sequence order (UC04 nav, UC07 edit). */
public record SeriesPostItemDTO(
        String seriesPostItemId,
        String postId,
        String title,
        String slug,
        String summary,
        int sequenceNumber,
        boolean current) {
}
