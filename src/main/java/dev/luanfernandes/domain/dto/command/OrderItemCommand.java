package dev.luanfernandes.domain.dto.command;

import dev.luanfernandes.domain.valueobject.ProductId;

public record OrderItemCommand(ProductId productId, Integer quantity) {}
