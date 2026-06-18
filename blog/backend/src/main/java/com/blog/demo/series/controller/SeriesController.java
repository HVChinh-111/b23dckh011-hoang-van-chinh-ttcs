package com.blog.demo.series.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blog.demo.common.response.ApiResponse;
import com.blog.demo.series.dto.SeriesCreateRequestDTO;
import com.blog.demo.series.dto.SeriesEditDTO;
import com.blog.demo.series.dto.SeriesListItemDTO;
import com.blog.demo.series.dto.SeriesPostPageDTO;
import com.blog.demo.series.dto.SeriesUpdateRequestDTO;
import com.blog.demo.series.service.SeriesService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;

    /** Public: list of all series with post counts. */
    @GetMapping
    public ApiResponse<List<SeriesListItemDTO>> getAll() {
        return ApiResponse.of(seriesService.getAllSeries());
    }

    /** Public: published posts of a series in sequence order (UC02B). */
    @GetMapping("/{slug}/posts")
    public ApiResponse<SeriesPostPageDTO> getPostsBySeries(
            @PathVariable String slug,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.of(seriesService.getPublishedPostsBySeries(slug, page, size));
    }

    /** Admin: series details for editing (UC07). */
    @GetMapping("/{id}")
    public ApiResponse<SeriesEditDTO> getForEdit(@PathVariable String id) {
        return ApiResponse.of(seriesService.getSeriesForEdit(id));
    }

    /** Admin: create a series (UC07). */
    @PostMapping
    public ApiResponse<SeriesListItemDTO> create(@Valid @RequestBody SeriesCreateRequestDTO request) {
        return ApiResponse.of(seriesService.createSeries(request));
    }

    /** Admin: update a series and reorder its posts (UC07 / BR07.3). */
    @PutMapping("/{id}")
    public ApiResponse<SeriesListItemDTO> update(
            @PathVariable String id,
            @Valid @RequestBody SeriesUpdateRequestDTO request) {
        return ApiResponse.of(seriesService.updateSeries(id, request));
    }

    /** Admin: delete a series (UC07 / BR07.2). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        seriesService.deleteSeries(id);
        return ResponseEntity.noContent().build();
    }
}
