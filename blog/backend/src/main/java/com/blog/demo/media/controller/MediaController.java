package com.blog.demo.media.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blog.demo.common.response.ApiResponse;
import com.blog.demo.media.dto.ImageUploadResponseDTO;
import com.blog.demo.media.dto.MediaFileDTO;
import com.blog.demo.media.service.MediaService;

import lombok.RequiredArgsConstructor;

/**
 * Admin-only media uploads. Used by the Markdown editor to insert images.
 */
@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/images")
    public ApiResponse<ImageUploadResponseDTO> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "postId", required = false) String postId) {
        return ApiResponse.of(mediaService.uploadPostImage(file, postId));
    }

    @GetMapping("/images")
    public ApiResponse<List<MediaFileDTO>> listImages(@RequestParam String postId) {
        return ApiResponse.of(mediaService.getPostImages(postId));
    }

    @DeleteMapping("/images/{id}")
    public ApiResponse<Void> deleteImage(@PathVariable String id) {
        mediaService.deleteById(id);
        return ApiResponse.of(null);
    }
}
