package com.blog.demo.topic.dto;

/** Lightweight topic info embedded in post detail / filter pages. */
public record TopicInfoDTO(String id, String name, String slug, String description) {
}
