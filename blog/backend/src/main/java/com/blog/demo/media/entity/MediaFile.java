package com.blog.demo.media.entity;

import com.blog.demo.common.entity.BaseEntity;
import com.blog.demo.post.entity.Post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An uploaded file stored on the local file system. {@code post} is optional:
 * content images are linked to their post (so they can be cleaned up), avatars
 * are not.
 */
@Entity
@Table(name = "media_file")
@Getter
@Setter
@NoArgsConstructor
public class MediaFile extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "stored_file_name", nullable = false, unique = true)
    private String storedFileName;

    /** Public URL path (e.g. /uploads/posts/xxx.png). */
    @Column(name = "public_path", nullable = false, unique = true, length = 500)
    private String publicPath;

    /** Absolute or relative path on disk. */
    @Column(name = "storage_path", nullable = false, unique = true, length = 500)
    private String storagePath;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_type", nullable = false, length = 30)
    private MediaUsageTypeEnum usageType;
}
