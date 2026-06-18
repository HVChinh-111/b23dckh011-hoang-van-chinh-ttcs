package com.blog.demo.topic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TopicUpdateRequestDTO(
        @NotBlank(message = "Tên Topic không được để trống")
        @Size(max = 255)
        String name,

        @NotBlank(message = "Slug không được để trống")
        @Size(max = 255)
        String slug,

        String description) {
}
