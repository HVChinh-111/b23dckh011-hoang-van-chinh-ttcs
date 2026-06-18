package com.blog.demo.series.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.blog.demo.series.dto.SeriesListItemDTO;
import com.blog.demo.series.entity.Series;

public interface SeriesRepository extends JpaRepository<Series, String> {

    @Query("""
            SELECT new com.blog.demo.series.dto.SeriesListItemDTO(
                s.id, s.name, s.slug, s.description, COUNT(spi.id))
            FROM Series s
            LEFT JOIN SeriesPostItem spi ON spi.series = s
            GROUP BY s.id, s.name, s.slug, s.description
            ORDER BY s.name ASC
            """)
    List<SeriesListItemDTO> findAllWithPostCount();

    List<Series> findAllByOrderByNameAsc();

    Optional<Series> findBySlug(String slug);

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    boolean existsByNameIgnoreCaseAndIdNot(String name, String id);

    boolean existsBySlugAndIdNot(String slug, String id);
}
