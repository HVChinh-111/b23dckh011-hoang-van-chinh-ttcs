package com.blog.demo.profile.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blog.demo.common.response.ApiResponse;
import com.blog.demo.profile.dto.AuthorProfileDTO;
import com.blog.demo.profile.dto.ProfileUpdateRequestDTO;
import com.blog.demo.profile.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /** Public: author info for the homepage (UC01). */
    @GetMapping
    public ApiResponse<AuthorProfileDTO> getProfile() {
        return ApiResponse.of(profileService.getProfile());
    }

    /** Admin: update profile with optional avatar (UC11). */
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AuthorProfileDTO> updateProfile(
            @Valid @RequestPart("data") ProfileUpdateRequestDTO request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        return ApiResponse.of(profileService.updateProfile(request, avatar));
    }
}
