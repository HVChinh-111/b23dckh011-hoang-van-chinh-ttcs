package com.blog.demo.media.dto;

/**
 * Returned after a content image upload; {@code url} is inserted into the
 * Markdown editor (UC09 alt flow 4a).
 */
public record ImageUploadResponseDTO(String id, String url, String mimeType, String fileName) {
}
