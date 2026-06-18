package com.blog.demo.post.dto;

import java.time.LocalDateTime;

import com.blog.demo.post.entity.PostStatusEnum;

/** UC08: admin post listing row (all statuses). */
public record AdminPostListItemDTO(
        String id,
        String title,
        String slug,
        PostStatusEnum status,
        LocalDateTime updatedAt) {
}
