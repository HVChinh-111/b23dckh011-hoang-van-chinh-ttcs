package com.blog.demo.auth.entity;

import java.time.LocalDateTime;

import com.blog.demo.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The single administrator account (BR05.1). Only the password hash is stored.
 */
@Entity
@Table(name = "admin_account")
@Getter
@Setter
@NoArgsConstructor
public class AdminAccount extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AdminAccount(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
