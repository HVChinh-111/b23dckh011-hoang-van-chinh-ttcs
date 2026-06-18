package com.blog.demo.series.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SeriesCreateRequestDTO(
        @NotBlank(message = "Tên Series không được để trống")
        @Size(max = 255)
        String name,

        @NotBlank(message = "Slug không được để trống")
        @Size(max = 255)
        String slug,

        String description) {
}
