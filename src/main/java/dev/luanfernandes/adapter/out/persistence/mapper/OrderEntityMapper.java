package dev.luanfernandes.adapter.out.persistence.mapper;

import dev.luanfernandes.adapter.out.persistence.entity.OrderItemJpaEntity;
import dev.luanfernandes.adapter.out.persistence.entity.OrderJpaEntity;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OrderEntityMapper {

    public OrderDomain toDomain(OrderJpaEntity entity) {
        List<OrderItemDomain> items =
                entity.getItems().stream().map(this::toDomain).toList();

        return new OrderDomain(
                OrderId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                items,
                Money.of(entity.getTotalAmount()),
                mapStatus(entity.getStatus()),
                entity.getCreatedAt());
    }

    public OrderJpaEntity toEntity(OrderDomain domain) {
        OrderJpaEntity entity = new OrderJpaEntity(
                domain.getId().value(),
                domain.getUserId().value(),
                null,
                domain.getTotalAmount().value(),
                mapStatus(domain.getStatus()),
                domain.getCreatedAt());

        List<OrderItemJpaEntity> itemEntities =
                domain.getItems().stream().map(item -> toEntity(item, entity)).toList();

        entity.setItems(itemEntities);
        return entity;
    }

    private OrderItemDomain toDomain(OrderItemJpaEntity entity) {
        return new OrderItemDomain(
                ProductId.of(entity.getProductId()),
                entity.getProductName(),
                Money.of(entity.getUnitPrice()),
                entity.getQuantity());
    }

    private OrderItemJpaEntity toEntity(OrderItemDomain domain, OrderJpaEntity orderEntity) {
        var entity = new OrderItemJpaEntity(
                orderEntity.getId(),
                domain.getProductId().value(),
                domain.getProductName(),
                domain.getUnitPrice().value(),
                domain.getQuantity(),
                domain.getSubtotal().value());
        entity.setId(UUID.randomUUID().toString());
        return entity;
    }

    private OrderDomain.OrderStatus mapStatus(OrderJpaEntity.OrderStatus status) {
        return OrderDomain.OrderStatus.valueOf(status.name());
    }

    private OrderJpaEntity.OrderStatus mapStatus(OrderDomain.OrderStatus status) {
        return OrderJpaEntity.OrderStatus.valueOf(status.name());
    }
}
