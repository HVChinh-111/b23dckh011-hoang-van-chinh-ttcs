package com.blog.demo.profile.dto;

import com.blog.demo.media.dto.MediaFileDTO;

public record AuthorProfileDTO(
        String id,
        String fullName,
        String shortBio,
        String githubUrl,
        String linkedInUrl,
        String facebookUrl,
        String contactEmail,
        MediaFileDTO avatar) {
}
