package dev.luanfernandes.domain.valueobject;

import java.util.UUID;

public record UserId(String value) {

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    public static UserId of(UUID value) {
        return new UserId(value.toString());
    }

    public static UserId of(String value) {
        return new UserId(value);
    }
}
