package dev.luanfernandes.adapter.out.persistence.mapper;

import dev.luanfernandes.adapter.out.persistence.entity.ProductJpaEntity;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityMapper {

    public ProductDomain toDomain(ProductJpaEntity entity) {
        return new ProductDomain(
                ProductId.of(entity.getId()),
                entity.getName(),
                entity.getDescription(),
                Money.of(entity.getPrice()),
                entity.getCategory(),
                entity.getStockQuantity(),
                entity.getCreatedAt());
    }

    public ProductJpaEntity toEntity(ProductDomain domain) {
        return new ProductJpaEntity(
                domain.getId().value(),
                domain.getName(),
                domain.getDescription(),
                domain.getPrice().value(),
                domain.getCategory(),
                domain.getStockQuantity(),
                domain.getCreatedAt());
    }
}
