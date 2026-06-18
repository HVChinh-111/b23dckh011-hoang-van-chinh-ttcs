package com.blog.demo.series.dto;

/** Lightweight series info embedded in post detail / filter pages. */
public record SeriesInfoDTO(String id, String name, String slug, String description) {
}
