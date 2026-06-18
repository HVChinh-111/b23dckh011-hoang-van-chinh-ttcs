package com.blog.demo.topic.dto;

/** Admin/guest topic listing with the number of published posts (UC06). */
public record TopicListItemDTO(
        String id,
        String name,
        String slug,
        String description,
        long publishedPostCount) {
}
