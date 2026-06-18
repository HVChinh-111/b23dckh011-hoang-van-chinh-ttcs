package com.blog.demo.post.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.demo.post.entity.Post;
import com.blog.demo.post.entity.PostStatusEnum;

public interface PostRepository extends JpaRepository<Post, String> {

    /** Homepage: published posts newest-published first (UC01 / BR01.1). */
    Page<Post> findByStatusOrderByPublishedAtDesc(PostStatusEnum status, Pageable pageable);

    /** Post detail by slug, restricted to a status (UC04 / BR04.1). */
    Optional<Post> findBySlugAndStatus(String slug, PostStatusEnum status);

    /** Search by title (UC03 / BR03.1, BR03.2). */
    Page<Post> findByStatusAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            PostStatusEnum status, String keyword, Pageable pageable);

    /** Admin list: all statuses, most recently updated first (UC08). */
    Page<Post> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    /** Posts of a topic, published only, newest-created first (UC02 / BR02.2). */
    Page<Post> findByTopics_SlugAndStatusOrderByCreatedAtDesc(
            String topicSlug, PostStatusEnum status, Pageable pageable);

    boolean existsBySlug(String slug);

    /** Slug uniqueness excluding the post being edited (BR10.1). */
    boolean existsBySlugAndIdNot(String slug, String id);
}
