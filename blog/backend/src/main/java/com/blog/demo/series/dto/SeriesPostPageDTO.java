package com.blog.demo.series.dto;

import java.util.List;

import com.blog.demo.common.response.PageInfoDTO;

/** UC02B: a series header + its published posts in sequence order. */
public record SeriesPostPageDTO(
        SeriesInfoDTO seriesInfo,
        List<SeriesPostItemDTO> items,
        PageInfoDTO pagination) {
}
