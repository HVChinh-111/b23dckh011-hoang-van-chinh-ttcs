package com.blog.demo.series.mapper;

import org.mapstruct.Mapper;

import com.blog.demo.series.dto.SeriesInfoDTO;
import com.blog.demo.series.dto.SeriesOptionDTO;
import com.blog.demo.series.entity.Series;

@Mapper(componentModel = "spring")
public interface SeriesMapper {

    SeriesInfoDTO toInfo(Series series);

    SeriesOptionDTO toOption(Series series);
}
