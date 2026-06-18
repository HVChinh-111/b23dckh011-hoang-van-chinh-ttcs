package com.blog.demo.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.demo.auth.dto.LoginRequestDTO;
import com.blog.demo.auth.dto.LogoutRequestDTO;
import com.blog.demo.auth.dto.RefreshRequestDTO;
import com.blog.demo.auth.dto.TokenResponseDTO;
import com.blog.demo.auth.service.AuthService;
import com.blog.demo.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ApiResponse.of(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO request) {
        return ApiResponse.of(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequestDTO request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
