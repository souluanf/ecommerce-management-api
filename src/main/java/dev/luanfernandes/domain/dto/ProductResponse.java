package dev.luanfernandes.domain.dto;

import dev.luanfernandes.domain.entity.ProductDomain;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        String id,
        String name,
        String description,
        BigDecimal price,
        String category,
        int stockQuantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    public static ProductResponse from(ProductDomain product) {
        return new ProductResponse(
                product.getId().value(),
                product.getName(),
                product.getDescription(),
                product.getPrice().value(),
                product.getCategory(),
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }
}
