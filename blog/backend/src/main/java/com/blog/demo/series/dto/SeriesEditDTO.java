package com.blog.demo.series.dto;

import java.util.List;

/** UC07 edit screen: series info + its ordered posts. */
public record SeriesEditDTO(
        String id,
        String name,
        String slug,
        String description,
        List<SeriesPostItemDTO> items) {
}
