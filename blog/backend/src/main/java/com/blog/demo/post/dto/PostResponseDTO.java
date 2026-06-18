package com.blog.demo.post.dto;

import java.time.LocalDateTime;

import com.blog.demo.post.entity.PostStatusEnum;

public record PostResponseDTO(
        String id,
        String title,
        String slug,
        PostStatusEnum status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime publishedAt) {
}
