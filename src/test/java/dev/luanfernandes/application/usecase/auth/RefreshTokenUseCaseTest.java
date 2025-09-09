package dev.luanfernandes.application.usecase.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.adapter.out.security.JwtTokenProviderAdapter;
import dev.luanfernandes.domain.dto.AuthenticationResponse;
import dev.luanfernandes.domain.dto.RefreshTokenRequest;
import dev.luanfernandes.domain.entity.UserDomain;
import dev.luanfernandes.domain.enums.UserRole;
import dev.luanfernandes.domain.exception.InvalidTokenException;
import dev.luanfernandes.domain.exception.UserNotFoundException;
import dev.luanfernandes.domain.port.out.auth.JwtTokenProvider;
import dev.luanfernandes.domain.port.out.auth.UserRepository;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.JwtToken;
import dev.luanfernandes.domain.valueobject.RefreshToken;
import dev.luanfernandes.domain.valueobject.UserId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for RefreshTokenUseCase")
class RefreshTokenUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtTokenProviderAdapter jwtTokenProviderAdapter;

    private RefreshTokenUseCase refreshTokenUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        refreshTokenUseCase = new RefreshTokenUseCase(userRepository, jwtTokenProvider, jwtTokenProviderAdapter);
    }

    @Test
    @DisplayName("Should refresh tokens successfully when refresh token is valid")
    void shouldRefreshTokens_WhenRefreshTokenIsValid() {
        String refreshTokenStr = "valid-refresh-token";
        String email = "test@example.com";
        RefreshTokenRequest request = new RefreshTokenRequest(refreshTokenStr);

        UserDomain user = createUser(email, UserRole.USER);
        JwtToken newAccessToken = createJwtToken();
        RefreshToken newRefreshToken = createRefreshToken();

        when(jwtTokenProvider.validateRefreshToken(refreshTokenStr)).thenReturn(true);
        when(jwtTokenProviderAdapter.extractEmailFromRefreshToken(refreshTokenStr))
                .thenReturn(email);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(email, user.getRole().getAuthority()))
                .thenReturn(newAccessToken);
        when(jwtTokenProvider.generateRefreshToken(email)).thenReturn(newRefreshToken);

        AuthenticationResponse response = refreshTokenUseCase.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo(newAccessToken.token());
        assertThat(response.refreshToken()).isEqualTo(newRefreshToken.token());
        assertThat(response.userEmail()).isEqualTo(email);
        assertThat(response.userRole()).isEqualTo(UserRole.USER.getRoleName());
        assertThat(response.expiresIn()).isGreaterThan(0);

        verify(jwtTokenProvider).validateRefreshToken(refreshTokenStr);
        verify(jwtTokenProviderAdapter).extractEmailFromRefreshToken(refreshTokenStr);
        verify(userRepository).findByEmail(any(Email.class));
        verify(jwtTokenProvider).generateAccessToken(email, user.getRole().getAuthority());
        verify(jwtTokenProvider).generateRefreshToken(email);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when refresh token is invalid")
    void shouldThrowInvalidTokenException_WhenRefreshTokenIsInvalid() {
        String refreshTokenStr = "invalid-refresh-token";
        RefreshTokenRequest request = new RefreshTokenRequest(refreshTokenStr);

        when(jwtTokenProvider.validateRefreshToken(refreshTokenStr)).thenReturn(false);

        assertThatThrownBy(() -> refreshTokenUseCase.execute(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid refresh token");

        verify(jwtTokenProvider).validateRefreshToken(refreshTokenStr);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found")
    void shouldThrowUserNotFoundException_WhenUserNotFound() {
        String refreshTokenStr = "valid-refresh-token";
        String email = "notfound@example.com";
        RefreshTokenRequest request = new RefreshTokenRequest(refreshTokenStr);

        when(jwtTokenProvider.validateRefreshToken(refreshTokenStr)).thenReturn(true);
        when(jwtTokenProviderAdapter.extractEmailFromRefreshToken(refreshTokenStr))
                .thenReturn(email);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenUseCase.execute(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(email);

        verify(jwtTokenProvider).validateRefreshToken(refreshTokenStr);
        verify(jwtTokenProviderAdapter).extractEmailFromRefreshToken(refreshTokenStr);
        verify(userRepository).findByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Should refresh tokens for admin user successfully")
    void shouldRefreshTokensForAdminUser_WhenRefreshTokenIsValid() {
        String refreshTokenStr = "admin-refresh-token";
        String email = "admin@example.com";
        RefreshTokenRequest request = new RefreshTokenRequest(refreshTokenStr);

        UserDomain adminUser = createUser(email, UserRole.ADMIN);
        JwtToken newAccessToken = createJwtToken();
        RefreshToken newRefreshToken = createRefreshToken();

        when(jwtTokenProvider.validateRefreshToken(refreshTokenStr)).thenReturn(true);
        when(jwtTokenProviderAdapter.extractEmailFromRefreshToken(refreshTokenStr))
                .thenReturn(email);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(adminUser));
        when(jwtTokenProvider.generateAccessToken(email, adminUser.getRole().getAuthority()))
                .thenReturn(newAccessToken);
        when(jwtTokenProvider.generateRefreshToken(email)).thenReturn(newRefreshToken);

        AuthenticationResponse response = refreshTokenUseCase.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.userRole()).isEqualTo(UserRole.ADMIN.getRoleName());
        assertThat(response.userEmail()).isEqualTo(email);
    }

    private UserDomain createUser(String email, UserRole role) {
        return new UserDomain(UserId.generate(), new Email(email), "hashedPassword", role, LocalDateTime.now());
    }

    private JwtToken createJwtToken() {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(3600);
        return new JwtToken("new-access-token", now, expiresAt);
    }

    private RefreshToken createRefreshToken() {
        return new RefreshToken("new-refresh-token", Instant.now().plusSeconds(86400));
    }
}
