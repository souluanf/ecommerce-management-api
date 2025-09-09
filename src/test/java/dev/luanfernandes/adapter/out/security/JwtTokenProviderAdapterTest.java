package dev.luanfernandes.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.exception.InvalidTokenException;
import dev.luanfernandes.domain.valueobject.JwtToken;
import dev.luanfernandes.domain.valueobject.RefreshToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderAdapterTest {

    private static final String ACCESS_TOKEN_SECRET =
            "my-super-secret-access-token-key-for-testing-purposes-minimum-32-chars";
    private static final String REFRESH_TOKEN_SECRET =
            "my-super-secret-refresh-token-key-for-testing-purposes-minimum-32-chars";
    private static final long ACCESS_TOKEN_EXPIRATION_MINUTES = 60;
    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 30;

    @Mock
    private Authentication authentication;

    private JwtTokenProviderAdapter jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProviderAdapter(
                ACCESS_TOKEN_SECRET,
                REFRESH_TOKEN_SECRET,
                ACCESS_TOKEN_EXPIRATION_MINUTES,
                REFRESH_TOKEN_EXPIRATION_DAYS);
    }

    @Test
    void shouldGenerateAccessTokenFromAuthentication() {

        String email = "user@example.com";
        String role = "ROLE_USER";

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        when(authentication.getName()).thenReturn(email);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        JwtToken token = jwtTokenProvider.generateAccessToken(authentication);

        assertThat(token).isNotNull();
        assertThat(token.token()).isNotBlank();
        assertThat(token.issuedAt()).isBeforeOrEqualTo(Instant.now());
        assertThat(token.expiresAt()).isAfter(Instant.now());
        assertThat(token.expiresAt()).isAfter(token.issuedAt());

        long expectedDurationMinutes = ChronoUnit.MINUTES.between(token.issuedAt(), token.expiresAt());
        assertThat(expectedDurationMinutes).isEqualTo(ACCESS_TOKEN_EXPIRATION_MINUTES);
    }

    @Test
    void shouldGenerateAccessTokenFromEmailAndRole() {

        String email = "admin@example.com";
        String role = "ROLE_ADMIN";

        JwtToken token = jwtTokenProvider.generateAccessToken(email, role);

        assertThat(token).isNotNull();
        assertThat(token.token()).isNotBlank();
        assertThat(token.issuedAt()).isBeforeOrEqualTo(Instant.now());
        assertThat(token.expiresAt()).isAfter(Instant.now());

        String extractedEmail = jwtTokenProvider.extractEmailFromToken(token.token());
        String extractedRole = jwtTokenProvider.extractRoleFromToken(token.token());

        assertThat(extractedEmail).isEqualTo(email);
        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    void shouldGenerateRefreshToken() {

        String email = "user@example.com";

        RefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(email);

        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken.token()).isNotBlank();
        assertThat(refreshToken.expiresAt()).isAfter(Instant.now());

        long daysBetween = ChronoUnit.DAYS.between(Instant.now(), refreshToken.expiresAt());
        assertThat(daysBetween).isBetween(REFRESH_TOKEN_EXPIRATION_DAYS - 1, REFRESH_TOKEN_EXPIRATION_DAYS);

        String extractedEmail = jwtTokenProvider.extractEmailFromRefreshToken(refreshToken.token());
        assertThat(extractedEmail).isEqualTo(email);
    }

    @ParameterizedTest
    @MethodSource("tokenDataProvider")
    void shouldExtractDataFromTokenAndValidate(String email, String role, String description) {
        JwtToken token = jwtTokenProvider.generateAccessToken(email, role);

        String extractedEmail = jwtTokenProvider.extractEmailFromToken(token.token());
        String extractedRole = jwtTokenProvider.extractRoleFromToken(token.token());
        boolean isValid = jwtTokenProvider.validateToken(token.token());

        assertThat(extractedEmail).isEqualTo(email);
        assertThat(extractedRole).isEqualTo(role);
        assertThat(isValid).isTrue();
    }

    private static Stream<Arguments> tokenDataProvider() {
        return Stream.of(
                Arguments.of("test@example.com", "ROLE_USER", "user role"),
                Arguments.of("admin@example.com", "ROLE_ADMIN", "admin role"),
                Arguments.of("user123@domain.com", "ROLE_USER", "another user"));
    }

    @Test
    void shouldRejectInvalidToken() {

        String invalidToken = "invalid.token.here";

        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldRejectExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes(StandardCharsets.UTF_8));
        Instant pastTime = Instant.now().minus(2, ChronoUnit.HOURS);
        Instant expiredTime = pastTime.plus(1, ChronoUnit.HOURS);

        String expiredToken = Jwts.builder()
                .subject("test@example.com")
                .claim("role", "ROLE_USER")
                .issuedAt(Date.from(pastTime))
                .expiration(Date.from(expiredTime))
                .signWith(key)
                .compact();

        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldValidateValidRefreshToken() {

        String email = "test@example.com";
        RefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(email);

        boolean isValid = jwtTokenProvider.validateRefreshToken(refreshToken.token());

        assertThat(isValid).isTrue();
    }

    @Test
    void shouldThrowExceptionForInvalidRefreshToken() {

        String invalidRefreshToken = "invalid.refresh.token";

        assertThatThrownBy(() -> jwtTokenProvider.validateRefreshToken(invalidRefreshToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    void shouldThrowExceptionForExpiredRefreshToken() {
        SecretKey key = Keys.hmacShaKeyFor(REFRESH_TOKEN_SECRET.getBytes(StandardCharsets.UTF_8));
        Instant pastTime = Instant.now().minus(2, ChronoUnit.DAYS);
        Instant expiredTime = pastTime.plus(1, ChronoUnit.DAYS);

        String expiredRefreshToken = Jwts.builder()
                .subject("test@example.com")
                .issuedAt(Date.from(pastTime))
                .expiration(Date.from(expiredTime))
                .signWith(key)
                .compact();

        assertThatThrownBy(() -> jwtTokenProvider.validateRefreshToken(expiredRefreshToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Refresh token expired");
    }

    @Test
    void shouldGetAuthenticationFromToken() {

        String email = "user@example.com";
        String role = "ROLE_USER";
        JwtToken token = jwtTokenProvider.generateAccessToken(email, role);

        Authentication auth = jwtTokenProvider.getAuthenticationFromToken(token.token());

        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo(email);
        assertThat(auth.getCredentials()).isNull();
        assertThat(auth.getAuthorities()).hasSize(1);
        assertThat(auth.getAuthorities().iterator().next().getAuthority()).isEqualTo(role);
    }

    @Test
    void shouldExtractEmailFromRefreshToken() {

        String email = "user@example.com";
        RefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(email);

        String extractedEmail = jwtTokenProvider.extractEmailFromRefreshToken(refreshToken.token());

        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    void shouldThrowExceptionWhenExtractingEmailFromInvalidToken() {

        String invalidToken = "invalid.token.here";

        assertThatThrownBy(() -> jwtTokenProvider.extractEmailFromToken(invalidToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid access token");
    }

    @Test
    void shouldThrowExceptionWhenExtractingRoleFromInvalidToken() {

        String invalidToken = "invalid.token.here";

        assertThatThrownBy(() -> jwtTokenProvider.extractRoleFromToken(invalidToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid access token");
    }

    @Test
    void shouldThrowExceptionWhenCreatingAuthenticationFromInvalidToken() {

        String invalidToken = "invalid.token.here";

        assertThatThrownBy(() -> jwtTokenProvider.getAuthenticationFromToken(invalidToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid access token");
    }

    @Test
    void shouldThrowExceptionWhenExtractingEmailFromInvalidRefreshToken() {

        String invalidRefreshToken = "invalid.refresh.token";

        assertThatThrownBy(() -> jwtTokenProvider.extractEmailFromRefreshToken(invalidRefreshToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    void shouldHandleMalformedToken() {

        String malformedToken = "malformed";

        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldHandleEmptyToken() {

        String emptyToken = "";

        boolean isValid = jwtTokenProvider.validateToken(emptyToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldHandleNullToken() {

        String nullToken = null;

        boolean isValid = jwtTokenProvider.validateToken(nullToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldGenerateTokensWithValidTimestamps() {

        String email = "user@example.com";
        String role = "ROLE_USER";

        JwtToken token1 = jwtTokenProvider.generateAccessToken(email, role);
        JwtToken token2 = jwtTokenProvider.generateAccessToken(email, role);

        assertThat(token1.issuedAt()).isBeforeOrEqualTo(Instant.now());
        assertThat(token2.issuedAt()).isBeforeOrEqualTo(Instant.now());
        assertThat(token1.expiresAt()).isAfter(token1.issuedAt());
        assertThat(token2.expiresAt()).isAfter(token2.issuedAt());
    }

    @Test
    void shouldGenerateDifferentRefreshTokensForSameUser() {

        String email = "user@example.com";

        RefreshToken refreshToken1 = jwtTokenProvider.generateRefreshToken(email);
        RefreshToken refreshToken2 = jwtTokenProvider.generateRefreshToken(email);

        assertThat(refreshToken1.token()).isNotEqualTo(refreshToken2.token());
    }

    @Test
    void shouldPreserveEmailCaseInTokens() {

        String email = "User@Example.COM";
        String role = "ROLE_USER";

        JwtToken token = jwtTokenProvider.generateAccessToken(email, role);
        String extractedEmail = jwtTokenProvider.extractEmailFromToken(token.token());

        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    void shouldHandleSpecialCharactersInEmail() {

        String email = "user+test@example-domain.com";
        String role = "ROLE_USER";

        JwtToken token = jwtTokenProvider.generateAccessToken(email, role);
        String extractedEmail = jwtTokenProvider.extractEmailFromToken(token.token());

        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    void shouldHandleUnsupportedToken() {

        String unsupportedToken = "eyJhbGciOiJub25lIn0.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0.";

        boolean isValid = jwtTokenProvider.validateToken(unsupportedToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldHandleTokenWithInvalidSignature() {

        String email = "test@example.com";
        String role = "ROLE_USER";
        JwtToken validToken = jwtTokenProvider.generateAccessToken(email, role);

        String tokenWithInvalidSignature =
                validToken.token().substring(0, validToken.token().lastIndexOf('.')) + ".invalid_signature";

        boolean isValid = jwtTokenProvider.validateToken(tokenWithInvalidSignature);

        assertThat(isValid).isFalse();
    }
}
