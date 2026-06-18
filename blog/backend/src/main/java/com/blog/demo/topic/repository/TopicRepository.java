package com.blog.demo.topic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.blog.demo.post.entity.PostStatusEnum;
import com.blog.demo.topic.dto.TopicListItemDTO;
import com.blog.demo.topic.entity.Topic;

public interface TopicRepository extends JpaRepository<Topic, String> {

    @Query("""
            SELECT new com.blog.demo.topic.dto.TopicListItemDTO(
                t.id, t.name, t.slug, t.description, COUNT(p.id))
            FROM Topic t
            LEFT JOIN t.posts p WITH p.status = :status
            GROUP BY t.id, t.name, t.slug, t.description
            ORDER BY t.name ASC
            """)
    List<TopicListItemDTO> findAllWithPublishedPostCount(@Param("status") PostStatusEnum status);

    List<Topic> findAllByOrderByNameAsc();

    Optional<Topic> findBySlug(String slug);

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    boolean existsByNameIgnoreCaseAndIdNot(String name, String id);

    boolean existsBySlugAndIdNot(String slug, String id);
}
