package com.blog.demo.auth.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.demo.auth.dto.LoginRequestDTO;
import com.blog.demo.auth.dto.TokenResponseDTO;
import com.blog.demo.auth.entity.AdminAccount;
import com.blog.demo.auth.entity.AdminSession;
import com.blog.demo.auth.repository.AdminAccountRepository;
import com.blog.demo.auth.repository.AdminSessionRepository;
import com.blog.demo.auth.security.TokenProvider;
import com.blog.demo.common.exception.InvalidCredentialsException;

import lombok.RequiredArgsConstructor;

/**
 * Authentication use cases: login (UC05), background refresh (BR05.4) and
 * logout with token revocation (UC12 / BR12.1).
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String GENERIC_AUTH_ERROR = "Tên đăng nhập hoặc mật khẩu không chính xác";
    private static final String BEARER = "Bearer";

    private final AdminAccountRepository adminAccountRepository;
    private final AdminSessionRepository adminSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public TokenResponseDTO login(LoginRequestDTO request) {
        AdminAccount account = adminAccountRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login failed: unknown email");
                    return new InvalidCredentialsException(GENERIC_AUTH_ERROR);
                });

        // BR05.3: compare against the BCrypt hash, never plain text.
        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            log.warn("Login failed: bad password for account {}", account.getId());
            throw new InvalidCredentialsException(GENERIC_AUTH_ERROR);
        }

        log.info("Admin {} logged in", account.getId());
        return issueTokens(account);
    }

    /**
     * Rotates a refresh token: validates it, revokes the old session and issues
     * a fresh access/refresh pair (BR05.4).
     */
    @Transactional
    public TokenResponseDTO refresh(String refreshToken) {
        String hash = tokenProvider.hashRefreshToken(refreshToken);
        AdminSession session = adminSessionRepository.findByRefreshTokenHash(hash)
                .orElseThrow(() -> new InvalidCredentialsException("Phiên đăng nhập không hợp lệ"));

        LocalDateTime now = LocalDateTime.now();
        if (!session.isActive(now)) {
            throw new InvalidCredentialsException("Phiên đăng nhập đã hết hạn hoặc bị thu hồi");
        }

        session.revoke(now);
        adminSessionRepository.save(session);
        return issueTokens(session.getAdminAccount());
    }

    /** Revokes the session tied to the given refresh token (idempotent). */
    @Transactional
    public void logout(String refreshToken) {
        String hash = tokenProvider.hashRefreshToken(refreshToken);
        adminSessionRepository.findByRefreshTokenHash(hash).ifPresent(session -> {
            if (session.getRevokedAt() == null) {
                session.revoke(LocalDateTime.now());
                adminSessionRepository.save(session);
                log.info("Session {} revoked on logout", session.getId());
            }
        });
    }

    private TokenResponseDTO issueTokens(AdminAccount account) {
        String accessToken = tokenProvider.generateAccessToken(account);
        String refreshToken = tokenProvider.generateRefreshToken();
        LocalDateTime accessExpires = tokenProvider.getAccessTokenExpiresAt();
        LocalDateTime refreshExpires = tokenProvider.getRefreshTokenExpiresAt();

        AdminSession session = new AdminSession();
        session.setAdminAccount(account);
        session.setRefreshTokenHash(tokenProvider.hashRefreshToken(refreshToken));
        session.setIssuedAt(LocalDateTime.now());
        session.setRefreshTokenExpiresAt(refreshExpires);
        adminSessionRepository.save(session);

        return new TokenResponseDTO(accessToken, refreshToken, BEARER, accessExpires, refreshExpires);
    }
}
