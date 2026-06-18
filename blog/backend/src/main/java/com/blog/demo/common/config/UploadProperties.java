package com.blog.demo.common.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Local file-system upload configuration.
 */
@ConfigurationProperties(prefix = "app.upload")
public record UploadProperties(
        String dir,
        String publicBasePath,
        long maxContentImageSize,
        long maxAvatarSize,
        List<String> allowedImageTypes) {
}
