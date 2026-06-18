package com.blog.demo.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blog.demo.auth.dto.LoginRequestDTO;
import com.blog.demo.auth.dto.TokenResponseDTO;
import com.blog.demo.auth.entity.AdminAccount;
import com.blog.demo.auth.entity.AdminSession;
import com.blog.demo.auth.repository.AdminAccountRepository;
import com.blog.demo.auth.repository.AdminSessionRepository;
import com.blog.demo.auth.security.TokenProvider;
import com.blog.demo.common.exception.InvalidCredentialsException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock AdminAccountRepository adminAccountRepository;
    @Mock AdminSessionRepository adminSessionRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock TokenProvider tokenProvider;

    @InjectMocks AuthService authService;

    private AdminAccount account() {
        return new AdminAccount("admin@blog.local", "$2a$hash");
    }

    private void stubTokenIssuing() {
        when(tokenProvider.generateAccessToken(any())).thenReturn("access");
        when(tokenProvider.generateRefreshToken()).thenReturn("refresh-raw");
        when(tokenProvider.hashRefreshToken("refresh-raw")).thenReturn("refresh-hash");
        when(tokenProvider.getAccessTokenExpiresAt()).thenReturn(LocalDateTime.now().plusMinutes(30));
        when(tokenProvider.getRefreshTokenExpiresAt()).thenReturn(LocalDateTime.now().plusDays(30));
    }

    @Test
    void loginSucceedsAndPersistsSession() {
        AdminAccount account = account();
        when(adminAccountRepository.findByEmail("admin@blog.local")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("pw", "$2a$hash")).thenReturn(true);
        stubTokenIssuing();

        TokenResponseDTO result = authService.login(new LoginRequestDTO("admin@blog.local", "pw"));

        assertThat(result.accessToken()).isEqualTo("access");
        assertThat(result.refreshToken()).isEqualTo("refresh-raw");
        ArgumentCaptor<AdminSession> captor = ArgumentCaptor.forClass(AdminSession.class);
        verify(adminSessionRepository).save(captor.capture());
        // Only the hash is stored, never the raw refresh token.
        assertThat(captor.getValue().getRefreshTokenHash()).isEqualTo("refresh-hash");
    }

    @Test
    void loginWithWrongPasswordThrowsGenericError() {
        when(adminAccountRepository.findByEmail(anyString())).thenReturn(Optional.of(account()));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequestDTO("admin@blog.local", "bad")))
                .isInstanceOf(InvalidCredentialsException.class);
        verify(adminSessionRepository, never()).save(any());
    }

    @Test
    void loginWithUnknownEmailThrowsGenericError() {
        when(adminAccountRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequestDTO("nobody@blog.local", "pw")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void refreshRotatesTokenAndRevokesOldSession() {
        AdminSession session = new AdminSession();
        session.setAdminAccount(account());
        session.setRefreshTokenExpiresAt(LocalDateTime.now().plusDays(1));
        when(tokenProvider.hashRefreshToken("old-raw")).thenReturn("old-hash");
        when(adminSessionRepository.findByRefreshTokenHash("old-hash")).thenReturn(Optional.of(session));
        stubTokenIssuing();

        TokenResponseDTO result = authService.refresh("old-raw");

        assertThat(result.accessToken()).isEqualTo("access");
        assertThat(session.getRevokedAt()).isNotNull();          // old session revoked
        // one save for revoked old session + one for the new session
        verify(adminSessionRepository, org.mockito.Mockito.times(2)).save(any());
    }

    @Test
    void refreshRejectsRevokedSession() {
        AdminSession session = new AdminSession();
        session.setRefreshTokenExpiresAt(LocalDateTime.now().plusDays(1));
        session.revoke(LocalDateTime.now());
        when(tokenProvider.hashRefreshToken("x")).thenReturn("x-hash");
        when(adminSessionRepository.findByRefreshTokenHash("x-hash")).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> authService.refresh("x"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void logoutRevokesSession() {
        AdminSession session = new AdminSession();
        session.setRefreshTokenExpiresAt(LocalDateTime.now().plusDays(1));
        when(tokenProvider.hashRefreshToken("r")).thenReturn("r-hash");
        when(adminSessionRepository.findByRefreshTokenHash("r-hash")).thenReturn(Optional.of(session));

        authService.logout("r");

        assertThat(session.getRevokedAt()).isNotNull();
        verify(adminSessionRepository).save(session);
    }
}
