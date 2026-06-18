package com.blog.demo.topic.service;

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
import com.blog.demo.post.dto.PostSummaryDTO;
import com.blog.demo.post.entity.Post;
import com.blog.demo.post.entity.PostStatusEnum;
import com.blog.demo.post.mapper.PostMapper;
import com.blog.demo.post.repository.PostRepository;
import com.blog.demo.topic.dto.TopicCreateRequestDTO;
import com.blog.demo.topic.dto.TopicListItemDTO;
import com.blog.demo.topic.dto.TopicPostPageDTO;
import com.blog.demo.topic.dto.TopicResponseDTO;
import com.blog.demo.topic.dto.TopicUpdateRequestDTO;
import com.blog.demo.topic.entity.Topic;
import com.blog.demo.topic.mapper.TopicMapper;
import com.blog.demo.topic.repository.TopicRepository;

import lombok.RequiredArgsConstructor;

/**
 * Topic management (UC06) and topic-filtered post listing (UC02).
 */
@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Transactional(readOnly = true)
    public List<TopicListItemDTO> getAllTopics() {
        return topicRepository.findAllWithPublishedPostCount(PostStatusEnum.PUBLISHED);
    }

    @Transactional
    public TopicResponseDTO createTopic(TopicCreateRequestDTO request) {
        validateSlugFormat(request.slug());
        if (topicRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("DUPLICATE_TOPIC", "Tên Topic đã tồn tại");
        }
        if (topicRepository.existsBySlug(request.slug())) {
            throw new DuplicateResourceException("DUPLICATE_SLUG", "Slug Topic đã tồn tại");
        }
        Topic topic = new Topic(request.name().trim(), request.slug(), normalize(request.description()));
        return topicMapper.toResponse(topicRepository.save(topic));
    }

    @Transactional
    public TopicResponseDTO updateTopic(String id, TopicUpdateRequestDTO request) {
        Topic topic = findTopicById(id);
        validateSlugFormat(request.slug());
        if (topicRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new DuplicateResourceException("DUPLICATE_TOPIC", "Tên Topic đã tồn tại");
        }
        if (topicRepository.existsBySlugAndIdNot(request.slug(), id)) {
            throw new DuplicateResourceException("DUPLICATE_SLUG", "Slug Topic đã tồn tại");
        }
        topic.setName(request.name().trim());
        topic.setSlug(request.slug());
        topic.setDescription(normalize(request.description()));
        return topicMapper.toResponse(topicRepository.save(topic));
    }

    /**
     * Deletes a topic. The DB unlinks posts via ON DELETE CASCADE on the
     * post_topic join table; posts themselves are kept (BR06.2).
     */
    @Transactional
    public void deleteTopic(String id) {
        Topic topic = findTopicById(id);
        topicRepository.delete(topic);
    }

    @Transactional(readOnly = true)
    public TopicPostPageDTO getPublishedPostsByTopic(String slug, int page, int size) {
        Topic topic = topicRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("TOPIC_NOT_FOUND",
                        "Không tìm thấy chuyên mục"));   // UC02 exception flow 3a -> 404

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<Post> postPage = postRepository.findByTopics_SlugAndStatusOrderByCreatedAtDesc(
                slug, PostStatusEnum.PUBLISHED, pageable);

        List<PostSummaryDTO> posts = postPage.getContent().stream()
                .map(postMapper::toSummary)
                .toList();
        return new TopicPostPageDTO(topicMapper.toInfo(topic), posts, PageInfoDTO.from(postPage));
    }

    private Topic findTopicById(String id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TOPIC_NOT_FOUND",
                        "Không tìm thấy chuyên mục"));
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
