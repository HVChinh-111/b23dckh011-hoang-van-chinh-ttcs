package com.blog.demo.series.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.blog.demo.post.entity.PostStatusEnum;
import com.blog.demo.series.entity.SeriesPostItem;

public interface SeriesPostItemRepository extends JpaRepository<SeriesPostItem, String> {

    /** All items of a series in sequence order (admin edit / post detail nav). */
    List<SeriesPostItem> findBySeriesIdOrderBySequenceNumberAsc(String seriesId);

    /** Published items of a series in sequence order (post detail nav, guest). */
    List<SeriesPostItem> findBySeriesIdAndPostStatusOrderBySequenceNumberAsc(
            String seriesId, PostStatusEnum status);

    /** Published items of a series by slug, paginated in sequence order (UC02B). */
    Page<SeriesPostItem> findBySeries_SlugAndPost_StatusOrderBySequenceNumberAsc(
            String seriesSlug, PostStatusEnum status, Pageable pageable);

    Optional<SeriesPostItem> findByPostId(String postId);

    long countBySeriesId(String seriesId);

    void deleteByPostId(String postId);

    void deleteBySeriesId(String seriesId);

    @Query("SELECT COALESCE(MAX(spi.sequenceNumber), 0) FROM SeriesPostItem spi WHERE spi.series.id = :seriesId")
    int findMaxSequenceNumber(@Param("seriesId") String seriesId);
}
