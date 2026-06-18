package com.blog.demo.post.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.blog.demo.common.entity.BaseEntity;
import com.blog.demo.profile.entity.AuthorProfile;
import com.blog.demo.topic.entity.Topic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
public class Post extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_profile_id", nullable = false)
    private AuthorProfile author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "content_markdown", columnDefinition = "TEXT")
    private String contentMarkdown;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PostStatusEnum status = PostStatusEnum.DRAFT;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_topic",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private Set<Topic> topics = new HashSet<>();

    public boolean isPublished() {
        return status == PostStatusEnum.PUBLISHED;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
