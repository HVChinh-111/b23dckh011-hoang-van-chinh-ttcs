package com.blog.demo.auth.entity;

import java.time.LocalDateTime;

import com.blog.demo.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A login session backed by a refresh token. Only the SHA-256 hash of the
 * refresh token is persisted; sessions can be revoked (BR12.1).
 */
@Entity
@Table(name = "admin_session")
@Getter
@Setter
@NoArgsConstructor
public class AdminSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_account_id", nullable = false)
    private AdminAccount adminAccount;

    @Column(name = "refresh_token_hash", nullable = false, unique = true)
    private String refreshTokenHash;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "refresh_token_expires_at", nullable = false)
    private LocalDateTime refreshTokenExpiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    public boolean isActive(LocalDateTime now) {
        return revokedAt == null && refreshTokenExpiresAt.isAfter(now);
    }

    public void revoke(LocalDateTime when) {
        this.revokedAt = when;
    }
}
