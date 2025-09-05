package dev.luanfernandes.domain.valueobject;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Objects;

public record JwtToken(@NotBlank String token, Instant issuedAt, Instant expiresAt) {
    public JwtToken {
        Objects.requireNonNull(token, "Token cannot be null");
        Objects.requireNonNull(issuedAt, "IssuedAt cannot be null");
        Objects.requireNonNull(expiresAt, "ExpiresAt cannot be null");

        if (token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be blank");
        }

        if (issuedAt.isAfter(expiresAt)) {
            throw new IllegalArgumentException("IssuedAt cannot be after ExpiresAt");
        }
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }
}
