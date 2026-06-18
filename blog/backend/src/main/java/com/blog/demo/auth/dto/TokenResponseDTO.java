package com.blog.demo.auth.dto;

import java.time.LocalDateTime;

public record TokenResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        LocalDateTime accessTokenExpiresAt,
        LocalDateTime refreshTokenExpiresAt) {
}
