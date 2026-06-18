package com.blog.demo.post.dto;

import java.util.List;

import com.blog.demo.post.entity.PostStatusEnum;

/** UC10: existing post data pre-loaded into the editor. */
public record PostEditDTO(
        String id,
        String title,
        String slug,
        String contentMarkdown,
        PostStatusEnum status,
        List<String> selectedTopicIds,
        String selectedSeriesId) {
}
