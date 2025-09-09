package dev.luanfernandes.application.usecase.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.dto.AuthenticationResponse;
import dev.luanfernandes.domain.dto.LoginRequest;
import dev.luanfernandes.domain.entity.UserDomain;
import dev.luanfernandes.domain.enums.UserRole;
import dev.luanfernandes.domain.port.out.auth.JwtTokenProvider;
import dev.luanfernandes.domain.port.out.auth.UserRepository;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.JwtToken;
import dev.luanfernandes.domain.valueobject.RefreshToken;
import dev.luanfernandes.domain.valueobject.UserId;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for LoginUseCase")
class LoginUseCaseTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    @DisplayName("Should authenticate user successfully when credentials are valid")
    void shouldAuthenticateUser_WhenCredentialsAreValid() {
        String email = "test@example.com";
        String password = "password123";
        LoginRequest request = new LoginRequest(email, password);

        UserDomain user = createUser(email, UserRole.USER);
        JwtToken accessToken = createJwtToken();
        RefreshToken refreshToken = createRefreshToken();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(authentication)).thenReturn(accessToken);
        when(jwtTokenProvider.generateRefreshToken(email)).thenReturn(refreshToken);

        AuthenticationResponse response = loginUseCase.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo(accessToken.token());
        assertThat(response.refreshToken()).isEqualTo(refreshToken.token());
        assertThat(response.userEmail()).isEqualTo(email);
        assertThat(response.userRole()).isEqualTo(UserRole.USER.getRoleName());
        assertThat(response.expiresIn()).isGreaterThan(0);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(any(Email.class));
        verify(jwtTokenProvider).generateAccessToken(authentication);
        verify(jwtTokenProvider).generateRefreshToken(email);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when authentication fails")
    void shouldThrowBadCredentialsException_WhenAuthenticationFails() {

        String email = "test@example.com";
        String password = "wrongpassword";
        LoginRequest request = new LoginRequest(email, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(any(Email.class));
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(anyString());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when user not found")
    void shouldThrowBadCredentialsException_WhenUserNotFound() {

        String email = "notfound@example.com";
        String password = "password123";
        LoginRequest request = new LoginRequest(email, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(any(Email.class));
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(anyString());
    }

    @Test
    @DisplayName("Should authenticate admin user successfully")
    void shouldAuthenticateAdminUser_WhenCredentialsAreValid() {

        String email = "admin@example.com";
        String password = "adminpass";
        LoginRequest request = new LoginRequest(email, password);

        UserDomain adminUser = createUser(email, UserRole.ADMIN);
        JwtToken accessToken = createJwtToken();
        RefreshToken refreshToken = createRefreshToken();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(adminUser));
        when(jwtTokenProvider.generateAccessToken(authentication)).thenReturn(accessToken);
        when(jwtTokenProvider.generateRefreshToken(email)).thenReturn(refreshToken);

        AuthenticationResponse response = loginUseCase.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.userRole()).isEqualTo(UserRole.ADMIN.getRoleName());
        assertThat(response.userEmail()).isEqualTo(email);
    }

    private UserDomain createUser(String email, UserRole role) {
        return new UserDomain(
                UserId.generate(), new Email(email), "hashedPassword", role, java.time.LocalDateTime.now());
    }

    private JwtToken createJwtToken() {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(3600);
        return new JwtToken("test-access-token", now, expiresAt);
    }

    private RefreshToken createRefreshToken() {
        return new RefreshToken("test-refresh-token", Instant.now().plusSeconds(86400));
    }
}
