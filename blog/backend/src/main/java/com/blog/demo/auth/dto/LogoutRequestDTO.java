package com.blog.demo.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequestDTO(
        @NotBlank(message = "Refresh token không được để trống")
        String refreshToken) {
}
