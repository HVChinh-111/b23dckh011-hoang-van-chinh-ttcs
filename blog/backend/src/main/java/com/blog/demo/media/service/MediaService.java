package com.blog.demo.media.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.blog.demo.common.config.UploadProperties;
import com.blog.demo.common.exception.FileStorageException;
import com.blog.demo.common.exception.ResourceNotFoundException;
import com.blog.demo.media.dto.ImageUploadResponseDTO;
import com.blog.demo.media.dto.MediaFileDTO;
import com.blog.demo.media.entity.MediaFile;
import com.blog.demo.media.entity.MediaUsageTypeEnum;
import com.blog.demo.media.mapper.MediaMapper;
import com.blog.demo.media.repository.MediaFileRepository;
import com.blog.demo.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

/**
 * Validates and stores uploaded images (UC09 content images, UC11 avatar) and
 * cleans them up when their owning resource is removed (BR08.2 / BR11.2).
 */
@Service
@RequiredArgsConstructor
public class MediaService {

    private final FileStorageService fileStorageService;
    private final MediaFileRepository mediaFileRepository;
    private final UploadProperties uploadProperties;
    private final PostRepository postRepository;
    private final MediaMapper mediaMapper;

    @Transactional
    public ImageUploadResponseDTO uploadPostImage(MultipartFile file, String postId) {
        MediaFile media = storeImage(file, "posts", MediaUsageTypeEnum.POST_CONTENT,
                uploadProperties.maxContentImageSize());
        if (postId != null && !postId.isBlank()) {
            postRepository.findById(postId).ifPresent(media::setPost);
        }
        mediaFileRepository.save(media);
        return new ImageUploadResponseDTO(media.getId(), media.getPublicPath(),
                media.getMimeType(), media.getStoredFileName());
    }

    public List<MediaFileDTO> getPostImages(String postId) {
        return mediaFileRepository.findByPostId(postId).stream()
                .map(mediaMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteById(String id) {
        MediaFile media = mediaFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MediaFile", id));
        fileStorageService.delete(media.getStoragePath());
        mediaFileRepository.delete(media);
    }

    @Transactional
    public MediaFile uploadProfileAvatar(MultipartFile file) {
        return storeImage(file, "avatars", MediaUsageTypeEnum.PROFILE_AVATAR,
                uploadProperties.maxAvatarSize());
    }

    @Transactional
    public void deleteFile(MediaFile media) {
        if (media == null) {
            return;
        }
        fileStorageService.delete(media.getStoragePath());
        mediaFileRepository.delete(media);
    }

    /** Removes all content images linked to a post when the post is deleted. */
    @Transactional
    public void deleteByPost(String postId) {
        List<MediaFile> files = mediaFileRepository.findByPostId(postId);
        for (MediaFile file : files) {
            fileStorageService.delete(file.getStoragePath());
        }
        mediaFileRepository.deleteAll(files);
    }

    private MediaFile storeImage(MultipartFile file, String subDir,
            MediaUsageTypeEnum usageType, long maxSize) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Vui lòng chọn tệp ảnh để tải lên");
        }
        if (file.getSize() > maxSize) {
            throw new FileStorageException(
                    "Ảnh vượt quá dung lượng tối đa " + (maxSize / (1024 * 1024)) + "MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !uploadProperties.allowedImageTypes().contains(contentType)) {
            throw new FileStorageException("Định dạng ảnh không hợp lệ (chỉ hỗ trợ jpg, png, webp)");
        }

        FileStorageService.StoredFile stored = fileStorageService.store(file, subDir,
                extensionFor(contentType));

        MediaFile media = new MediaFile();
        media.setStoredFileName(stored.storedFileName());
        media.setPublicPath(stored.publicPath());
        media.setStoragePath(stored.storagePath());
        media.setMimeType(contentType);
        media.setUsageType(usageType);
        return mediaFileRepository.save(media);
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> throw new FileStorageException("Định dạng ảnh không hợp lệ");
        };
    }
}
