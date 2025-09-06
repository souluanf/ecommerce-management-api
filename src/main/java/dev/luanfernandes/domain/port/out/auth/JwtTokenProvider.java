package dev.luanfernandes.domain.port.out.auth;

import dev.luanfernandes.domain.valueobject.JwtToken;
import dev.luanfernandes.domain.valueobject.RefreshToken;
import org.springframework.security.core.Authentication;

public interface JwtTokenProvider {

    JwtToken generateAccessToken(Authentication authentication);

    JwtToken generateAccessToken(String email, String role);

    RefreshToken generateRefreshToken(String email);

    String extractEmailFromToken(String token);

    String extractRoleFromToken(String token);

    boolean validateToken(String token);

    boolean validateRefreshToken(String refreshToken);

    Authentication getAuthenticationFromToken(String token);
}
