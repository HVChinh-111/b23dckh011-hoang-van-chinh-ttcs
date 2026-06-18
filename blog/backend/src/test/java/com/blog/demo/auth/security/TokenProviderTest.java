package com.blog.demo.auth.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.blog.demo.auth.entity.AdminAccount;
import com.blog.demo.common.config.JwtProperties;

class TokenProviderTest {

    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties(
                "unit-test-secret-key-that-is-at-least-32-bytes-long!!",
                Duration.ofMinutes(30),
                Duration.ofDays(30),
                "test");
        tokenProvider = new TokenProvider(props);
    }

    @Test
    void accessTokenRoundTripsSubject() {
        AdminAccount account = new AdminAccount("admin@blog.local", "hash");
        ReflectionTestUtils.setField(account, "id", "admin-123");

        String token = tokenProvider.generateAccessToken(account);

        assertThat(tokenProvider.parseAccessTokenSubject(token)).isEqualTo("admin-123");
    }

    @Test
    void invalidTokenReturnsNullSubject() {
        assertThat(tokenProvider.parseAccessTokenSubject("not-a-jwt")).isNull();
    }

    @Test
    void refreshTokenHashIsDeterministicAndHex() {
        String token = tokenProvider.generateRefreshToken();

        String h1 = tokenProvider.hashRefreshToken(token);
        String h2 = tokenProvider.hashRefreshToken(token);

        assertThat(h1).isEqualTo(h2).hasSize(64).matches("[0-9a-f]+");
    }

    @Test
    void refreshTokensAreUnique() {
        assertThat(tokenProvider.generateRefreshToken())
                .isNotEqualTo(tokenProvider.generateRefreshToken());
    }

    @Test
    void expiryHelpersAreInTheFuture() {
        assertThat(tokenProvider.getAccessTokenExpiresAt()).isAfter(java.time.LocalDateTime.now());
        assertThat(tokenProvider.getRefreshTokenExpiresAt())
                .isAfter(tokenProvider.getAccessTokenExpiresAt());
    }
}
