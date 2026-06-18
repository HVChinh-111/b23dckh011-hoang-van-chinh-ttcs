package com.blog.demo.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.demo.auth.entity.AdminSession;

public interface AdminSessionRepository extends JpaRepository<AdminSession, String> {

    Optional<AdminSession> findByRefreshTokenHash(String refreshTokenHash);
}
