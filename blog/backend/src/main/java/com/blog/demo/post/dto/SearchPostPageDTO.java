package com.blog.demo.post.dto;

import java.util.List;

import com.blog.demo.common.response.PageInfoDTO;

/** UC03: search results with the (trimmed) keyword and total match count. */
public record SearchPostPageDTO(
        String keyword,
        long totalResults,
        List<PostSummaryDTO> posts,
        PageInfoDTO pagination) {
}
