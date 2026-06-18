package com.blog.demo.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.blog.demo.post.dto.AdminPostListItemDTO;
import com.blog.demo.post.dto.PostResponseDTO;
import com.blog.demo.post.dto.PostSummaryDTO;
import com.blog.demo.post.entity.Post;

@Mapper(componentModel = "spring", uses = com.blog.demo.topic.mapper.TopicMapper.class,
        imports = com.blog.demo.common.util.TextUtil.class)
public interface PostMapper {

    @Mapping(target = "summary",
            expression = "java(com.blog.demo.common.util.TextUtil.excerpt(post.getContentMarkdown(), 200))")
    PostSummaryDTO toSummary(Post post);

    PostResponseDTO toResponse(Post post);

    AdminPostListItemDTO toAdminListItem(Post post);
}
