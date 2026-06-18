package com.blog.demo.post.dto;

import java.util.List;

import com.blog.demo.common.response.PageInfoDTO;

/** UC08: paginated admin post list. */
public record AdminPostPageDTO(List<AdminPostListItemDTO> posts, PageInfoDTO pagination) {
}
