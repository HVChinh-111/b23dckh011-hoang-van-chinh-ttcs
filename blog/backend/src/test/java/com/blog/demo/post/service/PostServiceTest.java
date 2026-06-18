package com.blog.demo.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blog.demo.common.exception.ResourceNotFoundException;
import com.blog.demo.common.exception.ValidationException;
import com.blog.demo.common.util.MarkdownService;
import com.blog.demo.media.service.MediaService;
import com.blog.demo.post.dto.PostResponseDTO;
import com.blog.demo.post.dto.PostSaveRequestDTO;
import com.blog.demo.post.entity.Post;
import com.blog.demo.post.entity.PostStatusEnum;
import com.blog.demo.post.mapper.PostMapper;
import com.blog.demo.post.repository.PostRepository;
import com.blog.demo.profile.entity.AuthorProfile;
import com.blog.demo.profile.repository.AuthorProfileRepository;
import com.blog.demo.series.mapper.SeriesMapper;
import com.blog.demo.series.repository.SeriesPostItemRepository;
import com.blog.demo.series.repository.SeriesRepository;
import com.blog.demo.topic.mapper.TopicMapper;
import com.blog.demo.topic.repository.TopicRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock PostRepository postRepository;
    @Mock PostMapper postMapper;
    @Mock MarkdownService markdownService;
    @Mock AuthorProfileRepository authorProfileRepository;
    @Mock TopicRepository topicRepository;
    @Mock TopicMapper topicMapper;
    @Mock SeriesRepository seriesRepository;
    @Mock SeriesPostItemRepository seriesPostItemRepository;
    @Mock SeriesMapper seriesMapper;
    @Mock MediaService mediaService;

    @InjectMocks PostService postService;

    private PostSaveRequestDTO request(String slug, PostStatusEnum status) {
        return new PostSaveRequestDTO("Title", slug, "content", status, null, null);
    }

    @Test
    void createPostRejectsInvalidSlug() {
        assertThatThrownBy(() -> postService.createPost(request("Invalid Slug", PostStatusEnum.DRAFT)))
                .isInstanceOf(ValidationException.class);
        verify(postRepository, never()).save(any());
    }

    @Test
    void createPostRejectsDuplicateSlug() {
        when(postRepository.existsBySlug("my-post")).thenReturn(true);

        assertThatThrownBy(() -> postService.createPost(request("my-post", PostStatusEnum.DRAFT)))
                .isInstanceOf(ValidationException.class);
        verify(postRepository, never()).save(any());
    }

    @Test
    void createPublishedPostSetsPublishedAt() {
        when(postRepository.existsBySlug("my-post")).thenReturn(false);
        when(authorProfileRepository.findFirstByOrderByIdAsc())
                .thenReturn(Optional.of(new AuthorProfile()));
        when(postRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(postMapper.toResponse(any())).thenReturn(
                new PostResponseDTO("id", "Title", "my-post", PostStatusEnum.PUBLISHED, null, null, null));

        postService.createPost(request("my-post", PostStatusEnum.PUBLISHED));

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(PostStatusEnum.PUBLISHED);
        assertThat(captor.getValue().getPublishedAt()).isNotNull();
    }

    @Test
    void createDraftDoesNotSetPublishedAt() {
        when(postRepository.existsBySlug("my-post")).thenReturn(false);
        when(authorProfileRepository.findFirstByOrderByIdAsc())
                .thenReturn(Optional.of(new AuthorProfile()));
        when(postRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(postMapper.toResponse(any())).thenReturn(
                new PostResponseDTO("id", "Title", "my-post", PostStatusEnum.DRAFT, null, null, null));

        postService.createPost(request("my-post", PostStatusEnum.DRAFT));

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        assertThat(captor.getValue().getPublishedAt()).isNull();
    }

    @Test
    void updatePostRejectsDuplicateSlugExcludingSelf() {
        Post existing = new Post();
        existing.setStatus(PostStatusEnum.DRAFT);
        when(postRepository.findById("p1")).thenReturn(Optional.of(existing));
        when(postRepository.existsBySlugAndIdNot("taken", "p1")).thenReturn(true);

        assertThatThrownBy(() -> postService.updatePost("p1", request("taken", PostStatusEnum.DRAFT)))
                .isInstanceOf(ValidationException.class);
        verify(postRepository, never()).save(any());
    }

    @Test
    void searchRejectsBlankKeyword() {
        assertThatThrownBy(() -> postService.searchPosts("   ", 1, 10))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void postDetailMissingPublishedPostThrows404() {
        when(postRepository.findBySlugAndStatus(eq("x"), eq(PostStatusEnum.PUBLISHED)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPostDetail("x"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
