package com.blog.demo.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Credentials used to seed the single Admin account on first startup (BR05.1).
 */
@ConfigurationProperties(prefix = "app.admin")
public record AdminSeedProperties(String email, String password) {
}
