package com.blog.demo.auth.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.blog.demo.auth.entity.AdminAccount;
import com.blog.demo.common.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Issues and validates JWT access tokens and opaque refresh tokens.
 * Only a SHA-256 hash of each refresh token is ever persisted.
 */
@Component
public class TokenProvider {

    private final JwtProperties properties;
    private final SecretKey signingKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public TokenProvider(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(AdminAccount account) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = now.plus(properties.accessTokenExpiration());
        return Jwts.builder()
                .subject(account.getId())
                .claim("email", account.getEmail())
                .claim("role", "ADMIN")
                .issuer(properties.issuer())
                .issuedAt(toDate(now))
                .expiration(toDate(expires))
                .signWith(signingKey)
                .compact();
    }

    /** Generates a cryptographically random opaque refresh token. */
    public String generateRefreshToken() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** SHA-256 hex digest used for storing/looking up refresh tokens. */
    public String hashRefreshToken(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public LocalDateTime getAccessTokenExpiresAt() {
        return LocalDateTime.now().plus(properties.accessTokenExpiration());
    }

    public LocalDateTime getRefreshTokenExpiresAt() {
        return LocalDateTime.now().plus(properties.refreshTokenExpiration());
    }

    /**
     * Parses and validates an access token, returning the admin account id
     * (subject), or null if the token is invalid/expired.
     */
    public String parseAccessTokenSubject(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    private Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
