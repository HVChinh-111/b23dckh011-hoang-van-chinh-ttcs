package com.blog.demo.topic.mapper;

import org.mapstruct.Mapper;

import com.blog.demo.topic.dto.TopicInfoDTO;
import com.blog.demo.topic.dto.TopicOptionDTO;
import com.blog.demo.topic.dto.TopicResponseDTO;
import com.blog.demo.topic.entity.Topic;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    TopicResponseDTO toResponse(Topic topic);

    TopicInfoDTO toInfo(Topic topic);

    TopicOptionDTO toOption(Topic topic);
}
