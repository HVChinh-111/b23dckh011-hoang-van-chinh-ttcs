package com.blog.demo.post.dto;

import java.util.List;

import com.blog.demo.series.dto.SeriesOptionDTO;
import com.blog.demo.topic.dto.TopicOptionDTO;

/** UC09/UC10: selectable topics and series for the post editor. */
public record PostFormOptionsDTO(
        List<TopicOptionDTO> topics,
        List<SeriesOptionDTO> seriesList) {
}
