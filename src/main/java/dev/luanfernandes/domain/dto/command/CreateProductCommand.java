package dev.luanfernandes.domain.dto.command;

import dev.luanfernandes.domain.valueobject.Money;

public record CreateProductCommand(
        String name, String description, Money price, String category, Integer stockQuantity) {}
