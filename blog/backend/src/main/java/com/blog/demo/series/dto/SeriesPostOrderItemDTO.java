package com.blog.demo.series.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/** One entry in the reorder payload (UC07 flow 3b / BR07.3). */
public record SeriesPostOrderItemDTO(
        @NotBlank String seriesPostItemId,
        @Positive int sequenceNumber) {
}
