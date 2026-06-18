package com.blog.demo.topic.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blog.demo.common.exception.DuplicateResourceException;
import com.blog.demo.common.exception.ResourceNotFoundException;
import com.blog.demo.common.exception.ValidationException;
import com.blog.demo.post.mapper.PostMapper;
import com.blog.demo.post.repository.PostRepository;
import com.blog.demo.topic.dto.TopicCreateRequestDTO;
import com.blog.demo.topic.entity.Topic;
import com.blog.demo.topic.mapper.TopicMapper;
import com.blog.demo.topic.repository.TopicRepository;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock TopicRepository topicRepository;
    @Mock TopicMapper topicMapper;
    @Mock PostRepository postRepository;
    @Mock PostMapper postMapper;

    @InjectMocks TopicService topicService;

    @Test
    void createRejectsInvalidSlug() {
        assertThatThrownBy(() -> topicService.createTopic(
                new TopicCreateRequestDTO("Java", "Invalid Slug", null)))
                .isInstanceOf(ValidationException.class);
        verify(topicRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createRejectsDuplicateName() {
        when(topicRepository.existsByNameIgnoreCase("Java")).thenReturn(true);

        assertThatThrownBy(() -> topicService.createTopic(
                new TopicCreateRequestDTO("Java", "java", null)))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void createRejectsDuplicateSlug() {
        when(topicRepository.existsByNameIgnoreCase("Java")).thenReturn(false);
        when(topicRepository.existsBySlug("java")).thenReturn(true);

        assertThatThrownBy(() -> topicService.createTopic(
                new TopicCreateRequestDTO("Java", "java", null)))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void deleteRemovesTopicSoLinksAreCleared() {
        Topic topic = new Topic("Java", "java", null);
        when(topicRepository.findById("t1")).thenReturn(Optional.of(topic));

        topicService.deleteTopic("t1");

        verify(topicRepository).delete(topic);   // posts kept; join rows removed by DB cascade
    }

    @Test
    void topicPostsUnknownSlugThrows404() {
        when(topicRepository.findBySlug(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicService.getPublishedPostsByTopic("nope", 1, 10))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
