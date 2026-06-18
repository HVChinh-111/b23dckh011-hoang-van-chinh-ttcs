package com.blog.demo.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.blog.demo.topic.dto.TopicInfoDTO;

/** Post card shown in listings (home, topic, series, search). */
public record PostSummaryDTO(
        String id,
        String title,
        String slug,
        String summary,
        LocalDateTime publishedAt,
        List<TopicInfoDTO> topics) {
}
