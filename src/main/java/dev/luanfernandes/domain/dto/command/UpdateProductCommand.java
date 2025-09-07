package dev.luanfernandes.domain.dto.command;

import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;

public record UpdateProductCommand(
        ProductId id, String name, String description, Money price, String category, Integer stockQuantity) {}
