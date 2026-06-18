package com.blog.demo.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.blog.demo.series.dto.SeriesInfoDTO;
import com.blog.demo.series.dto.SeriesPostItemDTO;
import com.blog.demo.topic.dto.TopicInfoDTO;

/**
 * UC04: full post content rendered to HTML, plus TOC, topics and (optional)
 * series navigation. seriesInfo is null when the post is standalone.
 */
public record PostDetailDTO(
        String id,
        String title,
        String slug,
        String contentHtml,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime publishedAt,
        List<TopicInfoDTO> topics,
        List<TableOfContentItemDTO> tocItems,
        SeriesInfoDTO seriesInfo,
        List<SeriesPostItemDTO> seriesItems) {
}
