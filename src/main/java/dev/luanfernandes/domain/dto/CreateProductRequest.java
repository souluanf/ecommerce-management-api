package dev.luanfernandes.domain.dto;

import java.math.BigDecimal;

public record CreateProductRequest(
        String name, String description, BigDecimal price, String category, int stockQuantity) {}
