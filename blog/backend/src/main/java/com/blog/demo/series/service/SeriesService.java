package com.blog.demo.series.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.demo.common.exception.DuplicateResourceException;
import com.blog.demo.common.exception.ResourceNotFoundException;
import com.blog.demo.common.exception.ValidationException;
import com.blog.demo.common.response.PageInfoDTO;
import com.blog.demo.common.util.SlugUtil;
import com.blog.demo.common.util.TextUtil;
import com.blog.demo.post.entity.Post;
import com.blog.demo.post.entity.PostStatusEnum;
import com.blog.demo.series.dto.SeriesCreateRequestDTO;
import com.blog.demo.series.dto.SeriesEditDTO;
import com.blog.demo.series.dto.SeriesListItemDTO;
import com.blog.demo.series.dto.SeriesOptionDTO;
import com.blog.demo.series.dto.SeriesPostItemDTO;
import com.blog.demo.series.dto.SeriesPostOrderItemDTO;
import com.blog.demo.series.dto.SeriesPostPageDTO;
import com.blog.demo.series.entity.Series;
import com.blog.demo.series.entity.SeriesPostItem;
import com.blog.demo.series.mapper.SeriesMapper;
import com.blog.demo.series.repository.SeriesPostItemRepository;
import com.blog.demo.series.repository.SeriesRepository;

import lombok.RequiredArgsConstructor;

/**
 * Series management with post ordering (UC07) and series-filtered listing (UC02B).
 */
@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final SeriesPostItemRepository seriesPostItemRepository;
    private final SeriesMapper seriesMapper;

    @Transactional(readOnly = true)
    public List<SeriesListItemDTO> getAllSeries() {
        return seriesRepository.findAllWithPostCount();
    }

    @Transactional(readOnly = true)
    public List<SeriesOptionDTO> getSeriesOptions() {
        return seriesRepository.findAllByOrderByNameAsc().stream()
                .map(seriesMapper::toOption)
                .toList();
    }

    @Transactional(readOnly = true)
    public SeriesEditDTO getSeriesForEdit(String id) {
        Series series = findSeriesById(id);
        List<SeriesPostItemDTO> items = seriesPostItemRepository
                .findBySeriesIdOrderBySequenceNumberAsc(id).stream()
                .map(item -> toItemDto(item, null))
                .toList();
        return new SeriesEditDTO(series.getId(), series.getName(), series.getSlug(),
                series.getDescription(), items);
    }

    @Transactional
    public SeriesListItemDTO createSeries(SeriesCreateRequestDTO request) {
        validateSlugFormat(request.slug());
        if (seriesRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("DUPLICATE_SERIES", "Tên Series đã tồn tại");
        }
        if (seriesRepository.existsBySlug(request.slug())) {
            throw new DuplicateResourceException("DUPLICATE_SLUG", "Slug Series đã tồn tại");
        }
        Series series = seriesRepository.save(
                new Series(request.name().trim(), request.slug(), normalize(request.description())));
        return toListItem(series, 0L);
    }

    @Transactional
    public SeriesListItemDTO updateSeries(String id, com.blog.demo.series.dto.SeriesUpdateRequestDTO request) {
        Series series = findSeriesById(id);
        validateSlugFormat(request.slug());
        if (seriesRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new DuplicateResourceException("DUPLICATE_SERIES", "Tên Series đã tồn tại");
        }
        if (seriesRepository.existsBySlugAndIdNot(request.slug(), id)) {
            throw new DuplicateResourceException("DUPLICATE_SLUG", "Slug Series đã tồn tại");
        }
        series.setName(request.name().trim());
        series.setSlug(request.slug());
        series.setDescription(normalize(request.description()));
        seriesRepository.save(series);

        applyOrdering(id, request.postOrders());   // BR07.3

        long count = seriesPostItemRepository.countBySeriesId(id);
        return toListItem(series, count);
    }

    /**
     * Deletes a series. Items are removed by ON DELETE CASCADE; the posts
     * themselves are kept (BR07.2).
     */
    @Transactional
    public void deleteSeries(String id) {
        Series series = findSeriesById(id);
        seriesRepository.delete(series);
    }

    @Transactional(readOnly = true)
    public SeriesPostPageDTO getPublishedPostsBySeries(String slug, int page, int size) {
        Series series = seriesRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("SERIES_NOT_FOUND",
                        "Không tìm thấy chuỗi bài"));   // UC02B 404

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<SeriesPostItem> itemPage = seriesPostItemRepository
                .findBySeries_SlugAndPost_StatusOrderBySequenceNumberAsc(
                        slug, PostStatusEnum.PUBLISHED, pageable);

        List<SeriesPostItemDTO> items = itemPage.getContent().stream()
                .map(item -> toItemDto(item, null))
                .toList();
        return new SeriesPostPageDTO(seriesMapper.toInfo(series), items, PageInfoDTO.from(itemPage));
    }

    private void applyOrdering(String seriesId, List<SeriesPostOrderItemDTO> postOrders) {
        if (postOrders == null || postOrders.isEmpty()) {
            return;
        }
        for (SeriesPostOrderItemDTO order : postOrders) {
            SeriesPostItem item = seriesPostItemRepository.findById(order.seriesPostItemId())
                    .orElseThrow(() -> new ValidationException(
                            "Bài viết trong chuỗi không tồn tại"));
            if (!item.getSeries().getId().equals(seriesId)) {
                throw new ValidationException("Bài viết không thuộc chuỗi bài này");
            }
            item.setSequenceNumber(order.sequenceNumber());
            seriesPostItemRepository.save(item);
        }
    }

    private SeriesPostItemDTO toItemDto(SeriesPostItem item, String currentPostId) {
        Post post = item.getPost();
        boolean current = currentPostId != null && currentPostId.equals(post.getId());
        return new SeriesPostItemDTO(
                item.getId(),
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                TextUtil.excerpt(post.getContentMarkdown(), 160),
                item.getSequenceNumber(),
                current);
    }

    private SeriesListItemDTO toListItem(Series series, long count) {
        return new SeriesListItemDTO(series.getId(), series.getName(), series.getSlug(),
                series.getDescription(), count);
    }

    private Series findSeriesById(String id) {
        return seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SERIES_NOT_FOUND",
                        "Không tìm thấy chuỗi bài"));
    }

    private void validateSlugFormat(String slug) {
        if (!SlugUtil.isValid(slug)) {
            throw new ValidationException(
                    "Slug chỉ được chứa chữ thường, số và dấu gạch ngang");
        }
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
