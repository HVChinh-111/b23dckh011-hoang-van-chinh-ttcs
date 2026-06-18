package com.blog.demo.common.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Loads key=value pairs from a local {@code .env} file into the Spring
 * Environment so that {@code ${...}} placeholders resolve during local
 * development. Real OS environment variables and system properties take
 * precedence (the source is added with the lowest priority).
 *
 * Registered via
 * {@code META-INF/spring/org.springframework.boot.env.EnvironmentPostProcessor.imports}.
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String SOURCE_NAME = "dotenv";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path envFile = locateEnvFile();
        if (envFile == null) {
            return;
        }
        Map<String, Object> values = parse(envFile);
        if (!values.isEmpty()) {
            // addLast => OS env vars / system properties still win.
            environment.getPropertySources().addLast(new MapPropertySource(SOURCE_NAME, values));
        }
    }

    private Path locateEnvFile() {
        Path current = Paths.get(".env").toAbsolutePath();
        if (Files.exists(current)) {
            return current;
        }
        // Fallback: parent directory (useful when launched from a wrapper dir).
        Path parent = Paths.get("..", ".env").toAbsolutePath().normalize();
        return Files.exists(parent) ? parent : null;
    }

    private Map<String, Object> parse(Path envFile) {
        Map<String, Object> values = new LinkedHashMap<>();
        try {
            for (String rawLine : Files.readAllLines(envFile, StandardCharsets.UTF_8)) {
                String line = rawLine.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int eq = line.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = line.substring(0, eq).trim();
                String value = stripQuotes(line.substring(eq + 1).trim());
                values.put(key, value);
            }
        } catch (IOException e) {
            // Non-fatal: fall back to other property sources.
            System.err.println("Could not read .env file: " + e.getMessage());
        }
        return values;
    }

    private String stripQuotes(String value) {
        if (value.length() >= 2
                && ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'")))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
