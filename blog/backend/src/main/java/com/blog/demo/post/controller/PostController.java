package com.blog.demo.post.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blog.demo.common.response.ApiResponse;
import com.blog.demo.post.dto.PostDetailDTO;
import com.blog.demo.post.dto.PostPageDTO;
import com.blog.demo.post.dto.SearchPostPageDTO;
import com.blog.demo.post.service.PostService;

import lombok.RequiredArgsConstructor;

/**
 * Public (guest) post endpoints (UC01, UC03, UC04).
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ApiResponse<PostPageDTO> getPublishedPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.of(postService.getPublishedPosts(page, size));
    }

    @GetMapping("/search")
    public ApiResponse<SearchPostPageDTO> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.of(postService.searchPosts(keyword, page, size));
    }

    @GetMapping("/{slug}")
    public ApiResponse<PostDetailDTO> getDetail(@PathVariable String slug) {
        return ApiResponse.of(postService.getPostDetail(slug));
    }
}
