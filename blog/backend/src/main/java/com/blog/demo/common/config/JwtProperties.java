package com.blog.demo.common.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT configuration (signing secret + token lifetimes).
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        Duration accessTokenExpiration,
        Duration refreshTokenExpiration,
        String issuer) {
}
