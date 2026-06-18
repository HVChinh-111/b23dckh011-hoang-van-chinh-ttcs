package com.blog.demo.profile.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * URL/email format checks for contact fields are done in the service (BR11.1)
 * so that blank optional fields are accepted.
 */
public record ProfileUpdateRequestDTO(
        @NotBlank(message = "Họ tên không được để trống")
        String fullName,
        String shortBio,
        String githubUrl,
        String linkedInUrl,
        String facebookUrl,
        String contactEmail) {
}
