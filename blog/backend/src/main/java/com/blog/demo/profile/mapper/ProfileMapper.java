package com.blog.demo.profile.mapper;

import org.mapstruct.Mapper;

import com.blog.demo.media.mapper.MediaMapper;
import com.blog.demo.profile.dto.AuthorProfileDTO;
import com.blog.demo.profile.entity.AuthorProfile;

@Mapper(componentModel = "spring", uses = MediaMapper.class)
public interface ProfileMapper {

    AuthorProfileDTO toDto(AuthorProfile profile);
}
