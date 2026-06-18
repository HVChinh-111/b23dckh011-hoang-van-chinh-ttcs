package com.blog.demo.series.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.blog.demo.common.exception.DuplicateResourceException;
import com.blog.demo.post.entity.Post;
import com.blog.demo.series.dto.SeriesCreateRequestDTO;
import com.blog.demo.series.dto.SeriesPostOrderItemDTO;
import com.blog.demo.series.dto.SeriesUpdateRequestDTO;
import com.blog.demo.series.entity.Series;
import com.blog.demo.series.entity.SeriesPostItem;
import com.blog.demo.series.mapper.SeriesMapper;
import com.blog.demo.series.repository.SeriesPostItemRepository;
import com.blog.demo.series.repository.SeriesRepository;

@ExtendWith(MockitoExtension.class)
class SeriesServiceTest {

    @Mock SeriesRepository seriesRepository;
    @Mock SeriesPostItemRepository seriesPostItemRepository;
    @Mock SeriesMapper seriesMapper;

    @InjectMocks SeriesService seriesService;

    @Test
    void createRejectsDuplicateName() {
        when(seriesRepository.existsByNameIgnoreCase("Spring")).thenReturn(true);

        assertThatThrownBy(() -> seriesService.createSeries(
                new SeriesCreateRequestDTO("Spring", "spring", null)))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void updateAppliesNewSequenceNumbers() {
        Series series = new Series("Spring", "spring", null);
        ReflectionTestUtils.setField(series, "id", "s1");
        when(seriesRepository.findById("s1")).thenReturn(Optional.of(series));
        when(seriesRepository.existsByNameIgnoreCaseAndIdNot("Spring", "s1")).thenReturn(false);
        when(seriesRepository.existsBySlugAndIdNot("spring", "s1")).thenReturn(false);

        SeriesPostItem item = new SeriesPostItem(series, new Post(), 1);
        when(seriesPostItemRepository.findById("i1")).thenReturn(Optional.of(item));
        when(seriesPostItemRepository.countBySeriesId("s1")).thenReturn(1L);

        SeriesUpdateRequestDTO request = new SeriesUpdateRequestDTO(
                "Spring", "spring", null, List.of(new SeriesPostOrderItemDTO("i1", 5)));

        seriesService.updateSeries("s1", request);

        assertThat(item.getSequenceNumber()).isEqualTo(5);   // BR07.3
        verify(seriesPostItemRepository).save(item);
    }

    @Test
    void deleteRemovesSeriesSoPostsAreKept() {
        Series series = new Series("Spring", "spring", null);
        when(seriesRepository.findById("s1")).thenReturn(Optional.of(series));

        seriesService.deleteSeries("s1");

        verify(seriesRepository).delete(series);
    }
}
