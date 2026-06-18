package com.blog.demo.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.demo.profile.entity.AuthorProfile;

public interface AuthorProfileRepository extends JpaRepository<AuthorProfile, String> {

    /** The blog has a single author profile. */
    Optional<AuthorProfile> findFirstByOrderByIdAsc();
}
