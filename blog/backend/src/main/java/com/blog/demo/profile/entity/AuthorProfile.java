package com.blog.demo.profile.entity;

import com.blog.demo.auth.entity.AdminAccount;
import com.blog.demo.common.entity.BaseEntity;
import com.blog.demo.media.entity.MediaFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Public author profile shown on the homepage and edited by the admin (UC11).
 * One profile per admin account.
 */
@Entity
@Table(name = "author_profile")
@Getter
@Setter
@NoArgsConstructor
public class AuthorProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_account_id", nullable = false, unique = true)
    private AdminAccount adminAccount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_media_file_id", unique = true)
    private MediaFile avatar;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "short_bio", columnDefinition = "TEXT")
    private String shortBio;

    @Column(name = "github_url", columnDefinition = "TEXT")
    private String githubUrl;

    @Column(name = "linked_in_url", columnDefinition = "TEXT")
    private String linkedInUrl;

    @Column(name = "facebook_url", columnDefinition = "TEXT")
    private String facebookUrl;

    @Column(name = "contact_email", columnDefinition = "TEXT")
    private String contactEmail;

    public AuthorProfile(AdminAccount adminAccount) {
        this.adminAccount = adminAccount;
    }
}
