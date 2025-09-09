package dev.luanfernandes.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class RefreshTokenTest {

    @Test
    void shouldCreateRefreshToken_WithValidData() {
        String token = "valid.refresh.token";
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

        RefreshToken refreshToken = new RefreshToken(token, expiresAt);

        assertThat(refreshToken.token()).isEqualTo(token);
        assertThat(refreshToken.expiresAt()).isEqualTo(expiresAt);
    }

    @Test
    void shouldThrowException_WhenTokenIsNull() {
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

        assertThatThrownBy(() -> new RefreshToken(null, expiresAt))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Refresh token cannot be null");
    }

    @Test
    void shouldThrowException_WhenExpiresAtIsNull() {
        String token = "valid.refresh.token";

        assertThatThrownBy(() -> new RefreshToken(token, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ExpiresAt cannot be null");
    }

    @Test
    void shouldThrowException_WhenTokenIsBlank() {
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

        assertThatThrownBy(() -> new RefreshToken("", expiresAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Refresh token cannot be blank");
    }

    @Test
    void shouldThrowException_WhenTokenIsWhitespace() {
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

        assertThatThrownBy(() -> new RefreshToken("   ", expiresAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Refresh token cannot be blank");
    }

    @Test
    void shouldReturnTrue_WhenTokenIsExpired() {
        String token = "expired.refresh.token";
        Instant expiresAt = Instant.now().minus(1, ChronoUnit.DAYS);

        RefreshToken refreshToken = new RefreshToken(token, expiresAt);

        assertThat(refreshToken.isExpired()).isTrue();
    }

    @Test
    void shouldReturnFalse_WhenTokenIsNotExpired() {
        String token = "valid.refresh.token";
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

        RefreshToken refreshToken = new RefreshToken(token, expiresAt);

        assertThat(refreshToken.isExpired()).isFalse();
    }

    @Test
    void shouldReturnTrue_WhenTokenIsExpiredAtSpecificTime() {
        String token = "refresh.token";
        Instant expiresAt = Instant.parse("2024-01-01T12:00:00Z");
        Instant checkTime = Instant.parse("2024-01-02T12:00:00Z");

        RefreshToken refreshToken = new RefreshToken(token, expiresAt);

        assertThat(refreshToken.isExpired(checkTime)).isTrue();
    }

    @Test
    void shouldReturnFalse_WhenTokenIsNotExpiredAtSpecificTime() {
        String token = "refresh.token";
        Instant expiresAt = Instant.parse("2024-01-07T12:00:00Z");
        Instant checkTime = Instant.parse("2024-01-01T12:00:00Z");

        RefreshToken refreshToken = new RefreshToken(token, expiresAt);

        assertThat(refreshToken.isExpired(checkTime)).isFalse();
    }

    @Test
    void shouldReturnFalse_WhenCheckTimeEqualsExpiresAt() {
        String token = "refresh.token";
        Instant expiresAt = Instant.parse("2024-01-07T12:00:00Z");

        RefreshToken refreshToken = new RefreshToken(token, expiresAt);

        assertThat(refreshToken.isExpired(expiresAt)).isFalse();
    }

    @Test
    void shouldReturnTrue_WhenCheckTimeIsJustAfterExpiration() {
        String token = "refresh.token";
        Instant expiresAt = Instant.parse("2024-01-07T12:00:00Z");
        Instant checkTime = expiresAt.plus(1, ChronoUnit.NANOS);

        RefreshToken refreshToken = new RefreshToken(token, expiresAt);

        assertThat(refreshToken.isExpired(checkTime)).isTrue();
    }

    @Test
    void shouldHandleEdgeCase_WithVeryLongToken() {
        String longToken = "a".repeat(1000);
        Instant expiresAt = Instant.now().plus(30, ChronoUnit.DAYS);

        RefreshToken refreshToken = new RefreshToken(longToken, expiresAt);

        assertThat(refreshToken.token()).hasSize(1000);
        assertThat(refreshToken.isExpired()).isFalse();
    }

    @Test
    void shouldHandleEdgeCase_WithSingleCharacterToken() {
        String singleCharToken = "a";
        Instant expiresAt = Instant.now().plus(30, ChronoUnit.DAYS);

        RefreshToken refreshToken = new RefreshToken(singleCharToken, expiresAt);

        assertThat(refreshToken.token()).isEqualTo("a");
        assertThat(refreshToken.isExpired()).isFalse();
    }
}
