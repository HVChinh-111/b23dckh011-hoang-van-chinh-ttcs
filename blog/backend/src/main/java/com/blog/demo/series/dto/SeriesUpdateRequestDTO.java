package com.blog.demo.series.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SeriesUpdateRequestDTO(
        @NotBlank(message = "Tên Series không được để trống")
        @Size(max = 255)
        String name,

        @NotBlank(message = "Slug không được để trống")
        @Size(max = 255)
        String slug,

        String description,

        /** New ordering of the posts already in the series (BR07.3). */
        @Valid
        List<SeriesPostOrderItemDTO> postOrders) {
}
