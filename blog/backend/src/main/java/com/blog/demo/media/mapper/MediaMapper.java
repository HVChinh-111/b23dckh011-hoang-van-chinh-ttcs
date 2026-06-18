package com.blog.demo.media.mapper;

import org.mapstruct.Mapper;

import com.blog.demo.media.dto.MediaFileDTO;
import com.blog.demo.media.entity.MediaFile;

@Mapper(componentModel = "spring")
public interface MediaMapper {

    MediaFileDTO toDto(MediaFile mediaFile);
}
