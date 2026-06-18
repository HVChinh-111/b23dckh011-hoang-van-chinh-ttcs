package com.blog.demo.topic.entity;

import java.util.HashSet;
import java.util.Set;

import com.blog.demo.common.entity.BaseEntity;
import com.blog.demo.post.entity.Post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "topic")
@Getter
@Setter
@NoArgsConstructor
public class Topic extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "topics")
    private Set<Post> posts = new HashSet<>();

    public Topic(String name, String slug, String description) {
        this.name = name;
        this.slug = slug;
        this.description = description;
    }
}
