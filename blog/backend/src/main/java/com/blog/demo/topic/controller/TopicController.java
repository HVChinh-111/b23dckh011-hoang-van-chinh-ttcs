package com.blog.demo.topic.controller;

import java.util.List;

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
import com.blog.demo.topic.dto.TopicCreateRequestDTO;
import com.blog.demo.topic.dto.TopicListItemDTO;
import com.blog.demo.topic.dto.TopicPostPageDTO;
import com.blog.demo.topic.dto.TopicResponseDTO;
import com.blog.demo.topic.dto.TopicUpdateRequestDTO;
import com.blog.demo.topic.service.TopicService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    /** Public: list of topics with published-post counts (menu + admin). */
    @GetMapping
    public ApiResponse<List<TopicListItemDTO>> getAll() {
        return ApiResponse.of(topicService.getAllTopics());
    }

    /** Public: published posts of a topic (UC02). */
    @GetMapping("/{slug}/posts")
    public ApiResponse<TopicPostPageDTO> getPostsByTopic(
            @PathVariable String slug,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.of(topicService.getPublishedPostsByTopic(slug, page, size));
    }

    /** Admin: create a topic (UC06). */
    @PostMapping
    public ApiResponse<TopicResponseDTO> create(@Valid @RequestBody TopicCreateRequestDTO request) {
        return ApiResponse.of(topicService.createTopic(request));
    }

    /** Admin: update a topic (UC06). */
    @PutMapping("/{id}")
    public ApiResponse<TopicResponseDTO> update(
            @PathVariable String id,
            @Valid @RequestBody TopicUpdateRequestDTO request) {
        return ApiResponse.of(topicService.updateTopic(id, request));
    }

    /** Admin: delete a topic (UC06 / BR06.2). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }
}
