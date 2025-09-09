package dev.luanfernandes.domain.entity;

import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ProductDomain {

    private final ProductId id;
    private final String name;
    private final String description;
    private final Money price;
    private final String category;
    private final int stockQuantity;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductDomain(
            ProductId id,
            String name,
            String description,
            Money price,
            String category,
            int stockQuantity,
            LocalDateTime createdAt) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasStock() {
        return stockQuantity > 0;
    }

    public boolean hasEnoughStock(int quantity) {
        return stockQuantity >= quantity;
    }

    public ProductDomain reduceStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalStateException(
                    "Insufficient stock. Available: " + stockQuantity + ", Required: " + quantity);
        }
        return new ProductDomain(id, name, description, price, category, stockQuantity - quantity, createdAt);
    }
}
