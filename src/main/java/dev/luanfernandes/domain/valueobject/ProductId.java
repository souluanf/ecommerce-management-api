package dev.luanfernandes.domain.valueobject;

import java.util.UUID;

public record ProductId(String value) {

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID().toString());
    }

    public static ProductId of(UUID value) {
        return new ProductId(value.toString());
    }

    public static ProductId of(String value) {
        return new ProductId(value);
    }
}
