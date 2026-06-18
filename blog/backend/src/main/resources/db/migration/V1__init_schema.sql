-- ============================================================================
-- Personal Blog CMS - initial schema (ERD - Hinh 29)
-- All ids are UUID strings (varchar(36)). Timestamps use DATETIME.
-- ============================================================================

-- --------------------------------------------------------------------------
-- admin_account: the single administrator (BR05.1)
-- --------------------------------------------------------------------------
CREATE TABLE admin_account (
    id            VARCHAR(36)  NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at    DATETIME     NOT NULL,
    CONSTRAINT pk_admin_account PRIMARY KEY (id),
    CONSTRAINT uq_admin_account_email UNIQUE (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- --------------------------------------------------------------------------
-- topic
-- --------------------------------------------------------------------------
CREATE TABLE topic (
    id          VARCHAR(36)  NOT NULL,
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL,
    description TEXT         NULL,
    CONSTRAINT pk_topic PRIMARY KEY (id),
    CONSTRAINT uq_topic_name UNIQUE (name),
    CONSTRAINT uq_topic_slug UNIQUE (slug)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- --------------------------------------------------------------------------
-- series
-- --------------------------------------------------------------------------
CREATE TABLE series (
    id          VARCHAR(36)  NOT NULL,
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL,
    description TEXT         NULL,
    CONSTRAINT pk_series PRIMARY KEY (id),
    CONSTRAINT uq_series_name UNIQUE (name),
    CONSTRAINT uq_series_slug UNIQUE (slug)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- --------------------------------------------------------------------------
-- author_profile (references admin_account; avatar FK added after media_file)
-- --------------------------------------------------------------------------
CREATE TABLE author_profile (
    id                   VARCHAR(36)  NOT NULL,
    admin_account_id     VARCHAR(36)  NOT NULL,
    avatar_media_file_id VARCHAR(36)  NULL,
    full_name            VARCHAR(255) NULL,
    short_bio            TEXT         NULL,
    github_url           TEXT         NULL,
    linked_in_url        TEXT         NULL,
    facebook_url         TEXT         NULL,
    contact_email        TEXT         NULL,
    CONSTRAINT pk_author_profile PRIMARY KEY (id),
    CONSTRAINT uq_author_profile_admin UNIQUE (admin_account_id),
    CONSTRAINT uq_author_profile_avatar UNIQUE (avatar_media_file_id),
    CONSTRAINT fk_author_profile_admin FOREIGN KEY (admin_account_id)
        REFERENCES admin_account (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- --------------------------------------------------------------------------
-- admin_session: refresh-token sessions (hashed token, revocable - BR12.1)
-- --------------------------------------------------------------------------
CREATE TABLE admin_session (
    id                       VARCHAR(36)  NOT NULL,
    admin_account_id         VARCHAR(36)  NOT NULL,
    refresh_token_hash       VARCHAR(255) NOT NULL,
    issued_at                DATETIME     NOT NULL,
    refresh_token_expires_at DATETIME     NOT NULL,
    revoked_at               DATETIME     NULL,
    CONSTRAINT pk_admin_session PRIMARY KEY (id),
    CONSTRAINT uq_admin_session_token UNIQUE (refresh_token_hash),
    CONSTRAINT fk_admin_session_admin FOREIGN KEY (admin_account_id)
        REFERENCES admin_account (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- --------------------------------------------------------------------------
-- post (references author_profile; media FK added after media_file table)
-- --------------------------------------------------------------------------
CREATE TABLE post (
    id               VARCHAR(36)  NOT NULL,
    author_profile_id VARCHAR(36) NOT NULL,
    title            VARCHAR(255) NOT NULL,
    slug             VARCHAR(255) NOT NULL,
    content_markdown TEXT         NULL,
    status           VARCHAR(30)  NOT NULL,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL,
    published_at     DATETIME     NULL,
    CONSTRAINT pk_post PRIMARY KEY (id),
    CONSTRAINT uq_post_slug UNIQUE (slug),
    CONSTRAINT fk_post_author FOREIGN KEY (author_profile_id)
        REFERENCES author_profile (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_post_status ON post (status);

-- --------------------------------------------------------------------------
-- media_file: uploaded images (optionally linked to a post)
-- --------------------------------------------------------------------------
CREATE TABLE media_file (
    id               VARCHAR(36)  NOT NULL,
    post_id          VARCHAR(36)  NULL,
    stored_file_name VARCHAR(255) NOT NULL,
    public_path      VARCHAR(500) NOT NULL,
    storage_path     VARCHAR(500) NOT NULL,
    mime_type        VARCHAR(100) NOT NULL,
    usage_type       VARCHAR(30)  NOT NULL,
    CONSTRAINT pk_media_file PRIMARY KEY (id),
    CONSTRAINT uq_media_stored_name UNIQUE (stored_file_name),
    CONSTRAINT uq_media_public_path UNIQUE (public_path),
    CONSTRAINT uq_media_storage_path UNIQUE (storage_path),
    CONSTRAINT fk_media_post FOREIGN KEY (post_id)
        REFERENCES post (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- avatar FK now that media_file exists
ALTER TABLE author_profile
    ADD CONSTRAINT fk_author_profile_avatar FOREIGN KEY (avatar_media_file_id)
        REFERENCES media_file (id) ON DELETE SET NULL;

-- --------------------------------------------------------------------------
-- post_topic: many-to-many between post and topic (BR06.2: delete unlinks)
-- --------------------------------------------------------------------------
CREATE TABLE post_topic (
    post_id  VARCHAR(36) NOT NULL,
    topic_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_post_topic PRIMARY KEY (post_id, topic_id),
    CONSTRAINT fk_post_topic_post FOREIGN KEY (post_id)
        REFERENCES post (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_topic_topic FOREIGN KEY (topic_id)
        REFERENCES topic (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_post_topic_topic ON post_topic (topic_id);

-- --------------------------------------------------------------------------
-- series_post_item: a post inside a series with an ordering (BR07.3)
-- post_id UNIQUE => a post belongs to at most one series
-- --------------------------------------------------------------------------
CREATE TABLE series_post_item (
    id              VARCHAR(36) NOT NULL,
    series_id       VARCHAR(36) NOT NULL,
    post_id         VARCHAR(36) NOT NULL,
    sequence_number INT         NOT NULL,
    CONSTRAINT pk_series_post_item PRIMARY KEY (id),
    CONSTRAINT uq_series_item_post UNIQUE (post_id),
    CONSTRAINT fk_series_item_series FOREIGN KEY (series_id)
        REFERENCES series (id) ON DELETE CASCADE,
    CONSTRAINT fk_series_item_post FOREIGN KEY (post_id)
        REFERENCES post (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_series_item_series ON series_post_item (series_id);
