package com.blog.demo.common.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serves uploaded files from the local upload directory under the public base
 * path (e.g. GET /uploads/**).
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    public WebMvcConfig(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadRoot = Paths.get(uploadProperties.dir()).toAbsolutePath().normalize();
        String pattern = uploadProperties.publicBasePath() + "/**";
        registry.addResourceHandler(pattern)
                .addResourceLocations(uploadRoot.toUri().toString());
    }
}
