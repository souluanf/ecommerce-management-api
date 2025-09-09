package dev.luanfernandes.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class JwtTokenTest {

    @Test
    void shouldCreateJwtToken_WithValidData() {
        String token = "valid.jwt.token";
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(1, ChronoUnit.HOURS);

        JwtToken jwtToken = new JwtToken(token, issuedAt, expiresAt);

        assertThat(jwtToken.token()).isEqualTo(token);
        assertThat(jwtToken.issuedAt()).isEqualTo(issuedAt);
        assertThat(jwtToken.expiresAt()).isEqualTo(expiresAt);
    }

    @Test
    void shouldThrowException_WhenTokenIsNull() {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(1, ChronoUnit.HOURS);

        assertThatThrownBy(() -> new JwtToken(null, issuedAt, expiresAt))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Token cannot be null");
    }

    @Test
    void shouldThrowException_WhenIssuedAtIsNull() {
        String token = "valid.jwt.token";
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);

        assertThatThrownBy(() -> new JwtToken(token, null, expiresAt))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("IssuedAt cannot be null");
    }

    @Test
    void shouldThrowException_WhenExpiresAtIsNull() {
        String token = "valid.jwt.token";
        Instant issuedAt = Instant.now();

        assertThatThrownBy(() -> new JwtToken(token, issuedAt, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ExpiresAt cannot be null");
    }

    @Test
    void shouldThrowException_WhenTokenIsBlank() {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(1, ChronoUnit.HOURS);

        assertThatThrownBy(() -> new JwtToken("", issuedAt, expiresAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token cannot be blank");
    }

    @Test
    void shouldThrowException_WhenTokenIsWhitespace() {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(1, ChronoUnit.HOURS);

        assertThatThrownBy(() -> new JwtToken("   ", issuedAt, expiresAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token cannot be blank");
    }

    @Test
    void shouldThrowException_WhenIssuedAtIsAfterExpiresAt() {
        String token = "valid.jwt.token";
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.minus(1, ChronoUnit.HOURS);

        assertThatThrownBy(() -> new JwtToken(token, issuedAt, expiresAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("IssuedAt cannot be after ExpiresAt");
    }

    @Test
    void shouldAllowCreation_WhenIssuedAtEqualsExpiresAt() {
        String token = "valid.jwt.token";
        Instant instant = Instant.now();

        JwtToken jwtToken = new JwtToken(token, instant, instant);

        assertThat(jwtToken.token()).isEqualTo(token);
        assertThat(jwtToken.issuedAt()).isEqualTo(instant);
        assertThat(jwtToken.expiresAt()).isEqualTo(instant);
    }

    @Test
    void shouldReturnTrue_WhenTokenIsExpired() {
        String token = "expired.jwt.token";
        Instant issuedAt = Instant.now().minus(2, ChronoUnit.HOURS);
        Instant expiresAt = Instant.now().minus(1, ChronoUnit.HOURS);

        JwtToken jwtToken = new JwtToken(token, issuedAt, expiresAt);

        assertThat(jwtToken.isExpired()).isTrue();
    }

    @Test
    void shouldReturnFalse_WhenTokenIsNotExpired() {
        String token = "valid.jwt.token";
        Instant issuedAt = Instant.now();
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);

        JwtToken jwtToken = new JwtToken(token, issuedAt, expiresAt);

        assertThat(jwtToken.isExpired()).isFalse();
    }

    @Test
    void shouldReturnTrue_WhenTokenIsExpiredAtSpecificTime() {
        String token = "jwt.token";
        Instant issuedAt = Instant.parse("2024-01-01T10:00:00Z");
        Instant expiresAt = Instant.parse("2024-01-01T11:00:00Z");
        Instant checkTime = Instant.parse("2024-01-01T12:00:00Z");

        JwtToken jwtToken = new JwtToken(token, issuedAt, expiresAt);

        assertThat(jwtToken.isExpired(checkTime)).isTrue();
    }

    @Test
    void shouldReturnFalse_WhenTokenIsNotExpiredAtSpecificTime() {
        String token = "jwt.token";
        Instant issuedAt = Instant.parse("2024-01-01T10:00:00Z");
        Instant expiresAt = Instant.parse("2024-01-01T11:00:00Z");
        Instant checkTime = Instant.parse("2024-01-01T10:30:00Z");

        JwtToken jwtToken = new JwtToken(token, issuedAt, expiresAt);

        assertThat(jwtToken.isExpired(checkTime)).isFalse();
    }

    @Test
    void shouldReturnTrue_WhenCheckTimeEqualsExpiresAt() {
        String token = "jwt.token";
        Instant issuedAt = Instant.parse("2024-01-01T10:00:00Z");
        Instant expiresAt = Instant.parse("2024-01-01T11:00:00Z");

        JwtToken jwtToken = new JwtToken(token, issuedAt, expiresAt);

        assertThat(jwtToken.isExpired(expiresAt)).isFalse();
    }

    @Test
    void shouldHandleEdgeCase_WhenCheckTimeIsJustAfterExpiration() {
        String token = "jwt.token";
        Instant issuedAt = Instant.parse("2024-01-01T10:00:00Z");
        Instant expiresAt = Instant.parse("2024-01-01T11:00:00Z");
        Instant checkTime = expiresAt.plus(1, ChronoUnit.NANOS);

        JwtToken jwtToken = new JwtToken(token, issuedAt, expiresAt);

        assertThat(jwtToken.isExpired(checkTime)).isTrue();
    }
}
