package com.blog.demo.common.entity;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

/**
 * Base for all entities: a String UUID primary key generated on persist.
 */
@MappedSuperclass
@Getter
public abstract class BaseEntity {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @PrePersist
    void assignId() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    /** Allows explicitly setting an id (e.g. tests or seeding). */
    protected void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof BaseEntity other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }
}
