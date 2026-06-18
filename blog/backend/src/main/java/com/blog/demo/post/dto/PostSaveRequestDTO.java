package com.blog.demo.post.dto;

import java.util.List;

import com.blog.demo.post.entity.PostStatusEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Create/update payload for a post (UC09 / UC10). */
public record PostSaveRequestDTO(
        @NotBlank(message = "Tiêu đề không được để trống")
        @Size(max = 255)
        String title,

        @NotBlank(message = "Slug không được để trống")
        @Size(max = 255)
        String slug,

        @NotBlank(message = "Nội dung không được để trống")
        String contentMarkdown,

        @NotNull(message = "Trạng thái không được để trống")
        PostStatusEnum status,

        List<String> topicIds,

        String seriesId) {
}
