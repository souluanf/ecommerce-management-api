package dev.luanfernandes.application.usecase.auth;

import dev.luanfernandes.adapter.out.security.JwtTokenProviderAdapter;
import dev.luanfernandes.domain.dto.AuthenticationResponse;
import dev.luanfernandes.domain.dto.RefreshTokenRequest;
import dev.luanfernandes.domain.entity.UserDomain;
import dev.luanfernandes.domain.exception.InvalidTokenException;
import dev.luanfernandes.domain.exception.UserNotFoundException;
import dev.luanfernandes.domain.port.out.auth.JwtTokenProvider;
import dev.luanfernandes.domain.port.out.auth.UserRepository;
import dev.luanfernandes.domain.valueobject.Email;
import dev.luanfernandes.domain.valueobject.JwtToken;
import dev.luanfernandes.domain.valueobject.RefreshToken;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenProviderAdapter jwtTokenProviderAdapter;

    @Transactional
    public AuthenticationResponse execute(RefreshTokenRequest request) {
        log.info("Attempting token refresh");

        String refreshTokenStr = request.refreshToken();

        // Validate refresh token
        if (!jwtTokenProvider.validateRefreshToken(refreshTokenStr)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        // Extract email from refresh token
        String email = jwtTokenProviderAdapter.extractEmailFromRefreshToken(refreshTokenStr);

        // Get user
        UserDomain user =
                userRepository.findByEmail(new Email(email)).orElseThrow(() -> new UserNotFoundException(email));

        // Generate new tokens
        JwtToken accessToken = jwtTokenProvider.generateAccessToken(
                user.getEmail().value(), user.getRole().getAuthority());
        RefreshToken newRefreshToken =
                jwtTokenProvider.generateRefreshToken(user.getEmail().value());

        log.info("Token refresh successful for user: {}", email);

        return AuthenticationResponse.of(
                accessToken.token(),
                newRefreshToken.token(),
                ChronoUnit.SECONDS.between(accessToken.issuedAt(), accessToken.expiresAt()),
                user.getEmail().value(),
                user.getRole().getRoleName());
    }
}
