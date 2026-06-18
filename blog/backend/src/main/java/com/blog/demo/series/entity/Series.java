package com.blog.demo.series.entity;

import com.blog.demo.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "series")
@Getter
@Setter
@NoArgsConstructor
public class Series extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Series(String name, String slug, String description) {
        this.name = name;
        this.slug = slug;
        this.description = description;
    }
}
