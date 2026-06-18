package com.blog.demo.topic.dto;

import java.util.List;

import com.blog.demo.common.response.PageInfoDTO;
import com.blog.demo.post.dto.PostSummaryDTO;

/** UC02: a topic header + its published posts (paginated). */
public record TopicPostPageDTO(
        TopicInfoDTO topicInfo,
        List<PostSummaryDTO> posts,
        PageInfoDTO pagination) {
}
