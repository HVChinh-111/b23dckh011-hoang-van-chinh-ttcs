package com.blog.demo.post.dto;

import java.util.List;

import com.blog.demo.common.response.PageInfoDTO;

/** UC01: paginated published posts for the homepage. */
public record PostPageDTO(List<PostSummaryDTO> posts, PageInfoDTO pagination) {
}
