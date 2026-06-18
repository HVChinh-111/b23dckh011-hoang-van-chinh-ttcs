package com.blog.demo.post.service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.demo.common.exception.ResourceNotFoundException;
import com.blog.demo.common.exception.ValidationException;
import com.blog.demo.common.response.PageInfoDTO;
import com.blog.demo.common.util.MarkdownService;
import com.blog.demo.common.util.SlugUtil;
import com.blog.demo.common.util.TextUtil;
import com.blog.demo.media.service.MediaService;
import com.blog.demo.post.dto.AdminPostPageDTO;
import com.blog.demo.post.dto.PostDetailDTO;
import com.blog.demo.post.dto.PostEditDTO;
import com.blog.demo.post.dto.PostFormOptionsDTO;
import com.blog.demo.post.dto.PostPageDTO;
import com.blog.demo.post.dto.PostResponseDTO;
import com.blog.demo.post.dto.PostSaveRequestDTO;
import com.blog.demo.post.dto.SearchPostPageDTO;
import com.blog.demo.post.dto.TableOfContentItemDTO;
import com.blog.demo.post.entity.Post;
import com.blog.demo.post.entity.PostStatusEnum;
import com.blog.demo.post.mapper.PostMapper;
import com.blog.demo.post.repository.PostRepository;
import com.blog.demo.profile.entity.AuthorProfile;
import com.blog.demo.profile.repository.AuthorProfileRepository;
import com.blog.demo.series.dto.SeriesInfoDTO;
import com.blog.demo.series.dto.SeriesPostItemDTO;
import com.blog.demo.series.entity.Series;
import com.blog.demo.series.entity.SeriesPostItem;
import com.blog.demo.series.mapper.SeriesMapper;
import com.blog.demo.series.repository.SeriesPostItemRepository;
import com.blog.demo.series.repository.SeriesRepository;
import com.blog.demo.topic.dto.TopicInfoDTO;
import com.blog.demo.topic.entity.Topic;
import com.blog.demo.topic.mapper.TopicMapper;
import com.blog.demo.topic.repository.TopicRepository;

import lombok.RequiredArgsConstructor;

/**
 * Post use cases: homepage list (UC01), search (UC03), detail (UC04), admin
 * list (UC08), create (UC09), edit (UC10) and delete.
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private static final Pattern HAS_ALNUM = Pattern.compile("[\\p{L}\\p{N}]");

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final MarkdownService markdownService;
    private final AuthorProfileRepository authorProfileRepository;
    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;
    private final SeriesRepository seriesRepository;
    private final SeriesPostItemRepository seriesPostItemRepository;
    private final SeriesMapper seriesMapper;
    private final MediaService mediaService;

    // ---------------------------------------------------------------- guest

    @Transactional(readOnly = true)
    public PostPageDTO getPublishedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<Post> postPage = postRepository.findByStatusOrderByPublishedAtDesc(
                PostStatusEnum.PUBLISHED, pageable);
        return new PostPageDTO(
                postPage.getContent().stream().map(postMapper::toSummary).toList(),
                PageInfoDTO.from(postPage));
    }

    @Transactional(readOnly = true)
    public SearchPostPageDTO searchPosts(String keyword, int page, int size) {
        String trimmed = keyword == null ? "" : keyword.trim();
        if (trimmed.isEmpty() || !HAS_ALNUM.matcher(trimmed).find()) {
            // UC03 exception flow 2a
            throw new ValidationException("Vui lòng nhập từ khóa hợp lệ để tìm kiếm");
        }
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<Post> postPage = postRepository
                .findByStatusAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
                        PostStatusEnum.PUBLISHED, trimmed, pageable);
        return new SearchPostPageDTO(
                trimmed,
                postPage.getTotalElements(),
                postPage.getContent().stream().map(postMapper::toSummary).toList(),
                PageInfoDTO.from(postPage));
    }

    @Transactional(readOnly = true)
    public PostDetailDTO getPostDetail(String slug) {
        // BR04.1: drafts are treated as non-existent for guests.
        Post post = postRepository.findBySlugAndStatus(slug, PostStatusEnum.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("POST_NOT_FOUND",
                        "Không tìm thấy bài viết"));

        MarkdownService.RenderedMarkdown rendered = markdownService.render(post.getContentMarkdown());
        List<TableOfContentItemDTO> toc = rendered.headings().stream()
                .map(h -> new TableOfContentItemDTO(h.id(), h.title(), h.level()))
                .toList();
        List<TopicInfoDTO> topics = post.getTopics().stream()
                .map(topicMapper::toInfo)
                .toList();

        SeriesInfoDTO seriesInfo = null;
        List<SeriesPostItemDTO> seriesItems = List.of();
        SeriesPostItem currentItem = seriesPostItemRepository.findByPostId(post.getId()).orElse(null);
        if (currentItem != null) {
            Series series = currentItem.getSeries();
            seriesInfo = seriesMapper.toInfo(series);
            seriesItems = seriesPostItemRepository
                    .findBySeriesIdAndPostStatusOrderBySequenceNumberAsc(
                            series.getId(), PostStatusEnum.PUBLISHED)
                    .stream()
                    .map(item -> toSeriesItemDto(item, post.getId()))
                    .toList();
        }

        return new PostDetailDTO(
                post.getId(), post.getTitle(), post.getSlug(), rendered.html(),
                post.getCreatedAt(), post.getUpdatedAt(), post.getPublishedAt(),
                topics, toc, seriesInfo, seriesItems);
    }

    // ---------------------------------------------------------------- admin

    @Transactional(readOnly = true)
    public AdminPostPageDTO getAdminPosts(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<Post> postPage = postRepository.findAllByOrderByUpdatedAtDesc(pageable);
        return new AdminPostPageDTO(
                postPage.getContent().stream().map(postMapper::toAdminListItem).toList(),
                PageInfoDTO.from(postPage));
    }

    @Transactional(readOnly = true)
    public PostFormOptionsDTO getPostFormOptions() {
        var topics = topicRepository.findAllByOrderByNameAsc().stream()
                .map(topicMapper::toOption)
                .toList();
        var seriesList = seriesRepository.findAllByOrderByNameAsc().stream()
                .map(seriesMapper::toOption)
                .toList();
        return new PostFormOptionsDTO(topics, seriesList);
    }

    @Transactional(readOnly = true)
    public PostEditDTO getPostForEdit(String id) {
        Post post = findPostById(id);
        List<String> topicIds = post.getTopics().stream().map(Topic::getId).toList();
        String seriesId = seriesPostItemRepository.findByPostId(id)
                .map(item -> item.getSeries().getId())
                .orElse(null);
        return new PostEditDTO(post.getId(), post.getTitle(), post.getSlug(),
                post.getContentMarkdown(), post.getStatus(), topicIds, seriesId);
    }

    @Transactional
    public PostResponseDTO createPost(PostSaveRequestDTO request) {
        validateSlugFormat(request.slug());
        if (postRepository.existsBySlug(request.slug())) {
            throw new ValidationException("DUPLICATE_SLUG", "Slug đã tồn tại");
        }

        AuthorProfile author = authorProfileRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException("PROFILE_NOT_FOUND",
                        "Chưa có hồ sơ tác giả"));

        Post post = new Post();
        post.setAuthor(author);
        post.setTitle(request.title().trim());
        post.setSlug(request.slug());
        post.setContentMarkdown(request.contentMarkdown());
        post.setStatus(request.status());
        if (request.status() == PostStatusEnum.PUBLISHED) {
            post.setPublishedAt(LocalDateTime.now());
        }
        post.setTopics(resolveTopics(request.topicIds()));
        post = postRepository.save(post);

        linkToSeries(post, request.seriesId());
        return postMapper.toResponse(post);
    }

    @Transactional
    public PostResponseDTO updatePost(String id, PostSaveRequestDTO request) {
        Post post = findPostById(id);
        validateSlugFormat(request.slug());
        if (postRepository.existsBySlugAndIdNot(request.slug(), id)) {   // BR10.1
            throw new ValidationException("DUPLICATE_SLUG", "Slug đã tồn tại");
        }

        applyStatusTransition(post, request.status());

        post.setTitle(request.title().trim());
        post.setSlug(request.slug());
        post.setContentMarkdown(request.contentMarkdown());
        post.setTopics(resolveTopics(request.topicIds()));
        postRepository.save(post);

        relinkSeries(post, request.seriesId());
        return postMapper.toResponse(post);
    }

    @Transactional
    public void deletePost(String id) {
        Post post = findPostById(id);
        seriesPostItemRepository.deleteByPostId(id);   // remove from series
        mediaService.deleteByPost(id);                 // BR08.2: clean up images
        postRepository.delete(post);                   // join rows removed by Hibernate/DB
    }

    // ------------------------------------------------------------- helpers

    private void applyStatusTransition(Post post, PostStatusEnum newStatus) {
        boolean wasPublished = post.getStatus() == PostStatusEnum.PUBLISHED;
        if (newStatus == PostStatusEnum.PUBLISHED && !wasPublished) {
            post.setPublishedAt(LocalDateTime.now());
        } else if (newStatus == PostStatusEnum.DRAFT && wasPublished) {
            post.setPublishedAt(null);
        }
        post.setStatus(newStatus);
    }

    private Set<Topic> resolveTopics(List<String> topicIds) {
        Set<Topic> topics = new LinkedHashSet<>();
        if (topicIds == null) {
            return topics;
        }
        for (String topicId : topicIds) {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new ValidationException("Topic không tồn tại: " + topicId));
            topics.add(topic);
        }
        return topics;
    }

    private void linkToSeries(Post post, String seriesId) {
        if (seriesId == null || seriesId.isBlank()) {
            return;
        }
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new ValidationException("Series không tồn tại"));
        int next = seriesPostItemRepository.findMaxSequenceNumber(seriesId) + 1;
        seriesPostItemRepository.save(new SeriesPostItem(series, post, next));
    }

    private void relinkSeries(Post post, String seriesId) {
        SeriesPostItem existing = seriesPostItemRepository.findByPostId(post.getId()).orElse(null);
        if (seriesId == null || seriesId.isBlank()) {
            if (existing != null) {
                seriesPostItemRepository.delete(existing);
            }
            return;
        }
        if (existing != null && existing.getSeries().getId().equals(seriesId)) {
            return;   // unchanged
        }
        if (existing != null) {
            seriesPostItemRepository.delete(existing);
        }
        linkToSeries(post, seriesId);
    }

    private SeriesPostItemDTO toSeriesItemDto(SeriesPostItem item, String currentPostId) {
        Post p = item.getPost();
        return new SeriesPostItemDTO(
                item.getId(), p.getId(), p.getTitle(), p.getSlug(),
                TextUtil.excerpt(p.getContentMarkdown(), 160),
                item.getSequenceNumber(),
                p.getId().equals(currentPostId));
    }

    private Post findPostById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("POST_NOT_FOUND",
                        "Bài viết không tồn tại hoặc đã bị xóa"));
    }

    private void validateSlugFormat(String slug) {
        if (!SlugUtil.isValid(slug)) {   // BR09.1
            throw new ValidationException(
                    "Slug chỉ được chứa chữ thường, số và dấu gạch ngang");
        }
    }
}
