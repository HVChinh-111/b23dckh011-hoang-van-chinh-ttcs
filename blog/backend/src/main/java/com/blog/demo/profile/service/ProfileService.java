package com.blog.demo.profile.service;

import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.blog.demo.common.exception.ResourceNotFoundException;
import com.blog.demo.common.exception.ValidationException;
import com.blog.demo.media.entity.MediaFile;
import com.blog.demo.media.service.MediaService;
import com.blog.demo.profile.dto.AuthorProfileDTO;
import com.blog.demo.profile.dto.ProfileUpdateRequestDTO;
import com.blog.demo.profile.entity.AuthorProfile;
import com.blog.demo.profile.mapper.ProfileMapper;
import com.blog.demo.profile.repository.AuthorProfileRepository;

import lombok.RequiredArgsConstructor;

/**
 * Reads (UC01) and updates (UC11) the author profile.
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private static final Pattern URL_PATTERN = Pattern.compile("^https?://.+", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final AuthorProfileRepository authorProfileRepository;
    private final ProfileMapper profileMapper;
    private final MediaService mediaService;

    @Transactional(readOnly = true)
    public AuthorProfileDTO getProfile() {
        return profileMapper.toDto(loadProfile());
    }

    @Transactional
    public AuthorProfileDTO updateProfile(ProfileUpdateRequestDTO request, MultipartFile avatar) {
        AuthorProfile profile = loadProfile();

        validateUrl(request.githubUrl(), "GitHub");
        validateUrl(request.linkedInUrl(), "LinkedIn");
        validateUrl(request.facebookUrl(), "Facebook");
        validateEmail(request.contactEmail());

        profile.setFullName(request.fullName().trim());
        profile.setShortBio(sanitize(request.shortBio()));      // NFR11.1: anti-XSS
        profile.setGithubUrl(normalize(request.githubUrl()));
        profile.setLinkedInUrl(normalize(request.linkedInUrl()));
        profile.setFacebookUrl(normalize(request.facebookUrl()));
        profile.setContactEmail(normalize(request.contactEmail()));

        if (avatar != null && !avatar.isEmpty()) {
            MediaFile oldAvatar = profile.getAvatar();
            MediaFile newAvatar = mediaService.uploadProfileAvatar(avatar);
            profile.setAvatar(newAvatar);
            authorProfileRepository.save(profile);
            // BR11.2: delete the previous avatar after re-linking.
            if (oldAvatar != null) {
                mediaService.deleteFile(oldAvatar);
            }
        } else {
            authorProfileRepository.save(profile);
        }

        return profileMapper.toDto(profile);
    }

    private AuthorProfile loadProfile() {
        return authorProfileRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException("PROFILE_NOT_FOUND",
                        "Chưa có hồ sơ tác giả"));
    }

    private void validateUrl(String value, String label) {
        if (StringUtils.hasText(value) && !URL_PATTERN.matcher(value.trim()).matches()) {
            throw new ValidationException("Đường dẫn " + label + " không hợp lệ");
        }
    }

    private void validateEmail(String value) {
        if (StringUtils.hasText(value) && !EMAIL_PATTERN.matcher(value.trim()).matches()) {
            throw new ValidationException("Email liên hệ không hợp lệ");
        }
    }

    private String sanitize(String value) {
        return value == null ? null : Jsoup.clean(value, Safelist.none());
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
