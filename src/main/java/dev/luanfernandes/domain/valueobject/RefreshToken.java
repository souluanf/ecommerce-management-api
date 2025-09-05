package dev.luanfernandes.domain.valueobject;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Objects;

public record RefreshToken(@NotBlank String token, Instant expiresAt) {
    public RefreshToken {
        Objects.requireNonNull(token, "Refresh token cannot be null");
        Objects.requireNonNull(expiresAt, "ExpiresAt cannot be null");

        if (token.isBlank()) {
            throw new IllegalArgumentException("Refresh token cannot be blank");
        }
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }
}
