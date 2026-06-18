package com.blog.demo.post.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blog.demo.common.response.ApiResponse;
import com.blog.demo.post.dto.AdminPostPageDTO;
import com.blog.demo.post.dto.PostEditDTO;
import com.blog.demo.post.dto.PostFormOptionsDTO;
import com.blog.demo.post.dto.PostResponseDTO;
import com.blog.demo.post.dto.PostSaveRequestDTO;
import com.blog.demo.post.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Admin post management endpoints (UC08, UC09, UC10).
 */
@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
public class AdminPostController {

    private final PostService postService;

    @GetMapping
    public ApiResponse<AdminPostPageDTO> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.of(postService.getAdminPosts(page, size));
    }

    @GetMapping("/form-options")
    public ApiResponse<PostFormOptionsDTO> formOptions() {
        return ApiResponse.of(postService.getPostFormOptions());
    }

    @GetMapping("/{id}")
    public ApiResponse<PostEditDTO> getForEdit(@PathVariable String id) {
        return ApiResponse.of(postService.getPostForEdit(id));
    }

    @PostMapping
    public ApiResponse<PostResponseDTO> create(@Valid @RequestBody PostSaveRequestDTO request) {
        return ApiResponse.of(postService.createPost(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<PostResponseDTO> update(
            @PathVariable String id,
            @Valid @RequestBody PostSaveRequestDTO request) {
        return ApiResponse.of(postService.updatePost(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
