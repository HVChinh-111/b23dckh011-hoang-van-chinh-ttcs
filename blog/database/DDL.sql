CREATE DATABASE IF NOT EXISTS personal_blog_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE personal_blog_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS series_post_item;
DROP TABLE IF EXISTS post_topic;
DROP TABLE IF EXISTS media_file;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS series;
DROP TABLE IF EXISTS topic;
DROP TABLE IF EXISTS author_profile;
DROP TABLE IF EXISTS admin_session;
DROP TABLE IF EXISTS admin_account;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE admin_account (
    id              CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_admin_account PRIMARY KEY (id),
    CONSTRAINT uk_admin_account_email UNIQUE (email),
    CONSTRAINT chk_admin_account_email_not_blank
        CHECK (TRIM(email) <> ''),
    CONSTRAINT chk_admin_account_password_hash_not_blank
        CHECK (TRIM(password_hash) <> '')
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE admin_session (
    id                         CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    admin_account_id            CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    refresh_token_hash          VARCHAR(255) NOT NULL,
    issued_at                   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    refresh_token_expires_at    DATETIME(6) NOT NULL,
    revoked_at                  DATETIME(6) NULL,

    CONSTRAINT pk_admin_session PRIMARY KEY (id),
    CONSTRAINT uk_admin_session_refresh_token_hash UNIQUE (refresh_token_hash),

    INDEX idx_admin_session_admin_account_id (admin_account_id),
    INDEX idx_admin_session_expires_at (refresh_token_expires_at),
    INDEX idx_admin_session_revoked_at (revoked_at),

    CONSTRAINT fk_admin_session_admin_account
        FOREIGN KEY (admin_account_id)
        REFERENCES admin_account (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT chk_admin_session_refresh_token_hash_not_blank
        CHECK (TRIM(refresh_token_hash) <> ''),
    CONSTRAINT chk_admin_session_expires_after_issued
        CHECK (refresh_token_expires_at > issued_at),
    CONSTRAINT chk_admin_session_revoked_after_issued
        CHECK (revoked_at IS NULL OR revoked_at >= issued_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE author_profile (
    id                    CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    admin_account_id       CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    avatar_media_file_id   CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NULL,
    full_name              VARCHAR(255) NOT NULL,
    short_bio              TEXT NULL,
    github_url             VARCHAR(500) NULL,
    linkedin_url           VARCHAR(500) NULL,
    facebook_url           VARCHAR(500) NULL,
    contact_email          VARCHAR(255) NOT NULL,

    CONSTRAINT pk_author_profile PRIMARY KEY (id),
    CONSTRAINT uk_author_profile_admin_account_id UNIQUE (admin_account_id),
    CONSTRAINT uk_author_profile_avatar_media_file_id UNIQUE (avatar_media_file_id),

    CONSTRAINT fk_author_profile_admin_account
        FOREIGN KEY (admin_account_id)
        REFERENCES admin_account (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT chk_author_profile_full_name_not_blank
        CHECK (TRIM(full_name) <> ''),
    CONSTRAINT chk_author_profile_contact_email_not_blank
        CHECK (TRIM(contact_email) <> '')
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE post (
    id                  CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    author_profile_id   CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    title               VARCHAR(255) NOT NULL,
    slug                VARCHAR(255) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    content_markdown    LONGTEXT NOT NULL,
    status              VARCHAR(30) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT 'DRAFT',
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    published_at        DATETIME(6) NULL,

    CONSTRAINT pk_post PRIMARY KEY (id),
    CONSTRAINT uk_post_slug UNIQUE (slug),

    INDEX idx_post_author_profile_id (author_profile_id),
    INDEX idx_post_status_published_at (status, published_at),
    INDEX idx_post_created_at (created_at),
    FULLTEXT INDEX ft_post_title (title),

    CONSTRAINT fk_post_author_profile
        FOREIGN KEY (author_profile_id)
        REFERENCES author_profile (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_post_title_not_blank
        CHECK (TRIM(title) <> ''),
    CONSTRAINT chk_post_slug_format
        CHECK (slug REGEXP '^[a-z0-9]+(-[a-z0-9]+)*$'),
    CONSTRAINT chk_post_content_not_empty
        CHECK (CHAR_LENGTH(content_markdown) > 0),
    CONSTRAINT chk_post_status_enum
        CHECK (status IN ('DRAFT', 'PUBLISHED')),
    CONSTRAINT chk_post_published_at_when_published
        CHECK (status <> 'PUBLISHED' OR published_at IS NOT NULL),
    CONSTRAINT chk_post_published_after_created
        CHECK (published_at IS NULL OR published_at >= created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE media_file (
    id                 CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    post_id            CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NULL,
    stored_file_name   VARCHAR(255) COLLATE utf8mb4_bin NOT NULL,
    public_path        VARCHAR(500) COLLATE utf8mb4_bin NOT NULL,
    storage_path       VARCHAR(500) COLLATE utf8mb4_bin NOT NULL,
    mime_type          VARCHAR(100) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    usage_type         VARCHAR(30) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,

    CONSTRAINT pk_media_file PRIMARY KEY (id),
    CONSTRAINT uk_media_file_stored_file_name UNIQUE (stored_file_name),
    CONSTRAINT uk_media_file_public_path UNIQUE (public_path),
    CONSTRAINT uk_media_file_storage_path UNIQUE (storage_path),

    INDEX idx_media_file_post_id (post_id),
    INDEX idx_media_file_usage_type (usage_type),

    CONSTRAINT fk_media_file_post
        FOREIGN KEY (post_id)
        REFERENCES post (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT chk_media_file_stored_file_name_not_blank
        CHECK (TRIM(stored_file_name) <> ''),
    CONSTRAINT chk_media_file_public_path_not_blank
        CHECK (TRIM(public_path) <> ''),
    CONSTRAINT chk_media_file_storage_path_not_blank
        CHECK (TRIM(storage_path) <> ''),
    CONSTRAINT chk_media_file_mime_type_format
        CHECK (mime_type REGEXP '^[A-Za-z0-9.+-]+/[A-Za-z0-9.+-]+$'),
    CONSTRAINT chk_media_file_usage_type_enum
        CHECK (usage_type IN ('POST_CONTENT', 'PROFILE_AVATAR'))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

ALTER TABLE author_profile
    ADD CONSTRAINT fk_author_profile_avatar_media_file
        FOREIGN KEY (avatar_media_file_id)
        REFERENCES media_file (id)
        ON UPDATE CASCADE
        ON DELETE SET NULL;

CREATE TABLE topic (
    id            CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    name          VARCHAR(255) NOT NULL,
    slug          VARCHAR(255) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    description   TEXT NULL,

    CONSTRAINT pk_topic PRIMARY KEY (id),
    CONSTRAINT uk_topic_name UNIQUE (name),
    CONSTRAINT uk_topic_slug UNIQUE (slug),

    CONSTRAINT chk_topic_name_not_blank
        CHECK (TRIM(name) <> ''),
    CONSTRAINT chk_topic_slug_format
        CHECK (slug REGEXP '^[a-z0-9]+(-[a-z0-9]+)*$')
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE post_topic (
    post_id    CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    topic_id   CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,

    CONSTRAINT pk_post_topic PRIMARY KEY (post_id, topic_id),
    INDEX idx_post_topic_topic_id (topic_id, post_id),

    CONSTRAINT fk_post_topic_post
        FOREIGN KEY (post_id)
        REFERENCES post (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_post_topic_topic
        FOREIGN KEY (topic_id)
        REFERENCES topic (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE series (
    id            CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    name          VARCHAR(255) NOT NULL,
    slug          VARCHAR(255) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    description   TEXT NULL,

    CONSTRAINT pk_series PRIMARY KEY (id),
    CONSTRAINT uk_series_name UNIQUE (name),
    CONSTRAINT uk_series_slug UNIQUE (slug),

    CONSTRAINT chk_series_name_not_blank
        CHECK (TRIM(name) <> ''),
    CONSTRAINT chk_series_slug_format
        CHECK (slug REGEXP '^[a-z0-9]+(-[a-z0-9]+)*$')
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE series_post_item (
    id                CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    series_id          CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    post_id            CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    sequence_number    INT UNSIGNED NOT NULL,

    CONSTRAINT pk_series_post_item PRIMARY KEY (id),
    CONSTRAINT uk_series_post_item_post_id UNIQUE (post_id),
    CONSTRAINT uk_series_post_item_series_sequence UNIQUE (series_id, sequence_number),

    INDEX idx_series_post_item_series_id (series_id),

    CONSTRAINT fk_series_post_item_series
        FOREIGN KEY (series_id)
        REFERENCES series (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_series_post_item_post
        FOREIGN KEY (post_id)
        REFERENCES post (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT chk_series_post_item_sequence_positive
        CHECK (sequence_number > 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Trigger bổ sung rule:
-- PROFILE_AVATAR không được gắn trực tiếp với post_id.
-- POST_CONTENT bắt buộc phải gắn với một post_id.
DELIMITER $$

CREATE TRIGGER trg_media_file_bi_validate_usage
BEFORE INSERT ON media_file
FOR EACH ROW
BEGIN
    IF NEW.usage_type = 'PROFILE_AVATAR' AND NEW.post_id IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'PROFILE_AVATAR media_file must not reference a post';
    END IF;

    IF NEW.usage_type = 'POST_CONTENT' AND NEW.post_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'POST_CONTENT media_file must reference a post';
    END IF;
END$$

CREATE TRIGGER trg_media_file_bu_validate_usage
BEFORE UPDATE ON media_file
FOR EACH ROW
BEGIN
    IF NEW.usage_type = 'PROFILE_AVATAR' AND NEW.post_id IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'PROFILE_AVATAR media_file must not reference a post';
    END IF;

    IF NEW.usage_type = 'POST_CONTENT' AND NEW.post_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'POST_CONTENT media_file must reference a post';
    END IF;
END$$

DELIMITER ;
