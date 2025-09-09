package dev.luanfernandes.adapter.out.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.luanfernandes.adapter.out.persistence.entity.OrderItemJpaEntity;
import dev.luanfernandes.adapter.out.persistence.entity.OrderJpaEntity;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderEntityMapperTest {

    private OrderEntityMapper orderEntityMapper;

    @BeforeEach
    void setUp() {
        orderEntityMapper = new OrderEntityMapper();
    }

    @Test
    void shouldMapOrderJpaEntityToDomain() {

        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        OrderItemJpaEntity itemEntity = new OrderItemJpaEntity(
                null, productId.toString(), "Test Product", new BigDecimal("29.99"), 2, new BigDecimal("59.98"));
        itemEntity.setId(UUID.randomUUID().toString());

        OrderJpaEntity orderEntity = new OrderJpaEntity(
                orderId.toString(),
                userId.toString(),
                List.of(itemEntity),
                new BigDecimal("59.98"),
                OrderJpaEntity.OrderStatus.PENDING,
                createdAt);

        itemEntity.setOrderId(orderEntity.getId());

        OrderDomain orderDomain = orderEntityMapper.toDomain(orderEntity);

        assertThat(orderDomain).isNotNull();
        assertThat(orderDomain.getId().value()).isEqualTo(orderId.toString());
        assertThat(orderDomain.getUserId().value()).isEqualTo(userId.toString());
        assertThat(orderDomain.getTotalAmount().value()).isEqualTo(new BigDecimal("59.98"));
        assertThat(orderDomain.getStatus()).isEqualTo(OrderDomain.OrderStatus.PENDING);
        assertThat(orderDomain.getCreatedAt()).isEqualTo(createdAt);

        assertThat(orderDomain.getItems()).hasSize(1);
        OrderItemDomain itemDomain = orderDomain.getItems().get(0);
        assertThat(itemDomain.getProductId().value()).isEqualTo(productId.toString());
        assertThat(itemDomain.getProductName()).isEqualTo("Test Product");
        assertThat(itemDomain.getUnitPrice().value()).isEqualTo(new BigDecimal("29.99"));
        assertThat(itemDomain.getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldMapOrderDomainToEntity() {

        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        OrderItemDomain itemDomain =
                new OrderItemDomain(ProductId.of(productId), "Test Product", Money.of(new BigDecimal("29.99")), 2);

        OrderDomain orderDomain = new OrderDomain(
                OrderId.of(orderId),
                UserId.of(userId),
                List.of(itemDomain),
                Money.of(new BigDecimal("59.98")),
                OrderDomain.OrderStatus.PENDING,
                createdAt);

        OrderJpaEntity orderEntity = orderEntityMapper.toEntity(orderDomain);

        assertThat(orderEntity).isNotNull();
        assertThat(orderEntity.getId()).isEqualTo(orderId.toString());
        assertThat(orderEntity.getUserId()).isEqualTo(userId.toString());
        assertThat(orderEntity.getTotalAmount()).isEqualTo(new BigDecimal("59.98"));
        assertThat(orderEntity.getStatus()).isEqualTo(OrderJpaEntity.OrderStatus.PENDING);
        assertThat(orderEntity.getCreatedAt()).isEqualTo(createdAt);

        assertThat(orderEntity.getItems()).hasSize(1);
        OrderItemJpaEntity itemEntity = orderEntity.getItems().get(0);
        assertThat(itemEntity.getProductId()).isEqualTo(productId.toString());
        assertThat(itemEntity.getProductName()).isEqualTo("Test Product");
        assertThat(itemEntity.getUnitPrice()).isEqualTo(new BigDecimal("29.99"));
        assertThat(itemEntity.getQuantity()).isEqualTo(2);
        assertThat(itemEntity.getSubtotal()).isEqualTo(new BigDecimal("59.98"));
        assertThat(itemEntity.getOrderId()).isEqualTo(orderEntity.getId());
        assertThat(itemEntity.getId()).isNotNull();
    }

    @Test
    void shouldMapAllOrderStatuses() {
        OrderJpaEntity.OrderStatus pendingJpa = OrderJpaEntity.OrderStatus.PENDING;
        OrderDomain.OrderStatus pendingDomain = OrderDomain.OrderStatus.valueOf(pendingJpa.name());
        assertThat(pendingDomain).isEqualTo(OrderDomain.OrderStatus.PENDING);

        OrderDomain.OrderStatus pendingDomainReverse = OrderDomain.OrderStatus.PENDING;
        OrderJpaEntity.OrderStatus pendingJpaReverse = OrderJpaEntity.OrderStatus.valueOf(pendingDomainReverse.name());
        assertThat(pendingJpaReverse).isEqualTo(OrderJpaEntity.OrderStatus.PENDING);

        OrderJpaEntity.OrderStatus paidJpa = OrderJpaEntity.OrderStatus.PAID;
        OrderDomain.OrderStatus paidDomain = OrderDomain.OrderStatus.valueOf(paidJpa.name());
        assertThat(paidDomain).isEqualTo(OrderDomain.OrderStatus.PAID);

        OrderJpaEntity.OrderStatus cancelledJpa = OrderJpaEntity.OrderStatus.CANCELLED;
        OrderDomain.OrderStatus cancelledDomain = OrderDomain.OrderStatus.valueOf(cancelledJpa.name());
        assertThat(cancelledDomain).isEqualTo(OrderDomain.OrderStatus.CANCELLED);
    }

    @Test
    void shouldHandleMultipleOrderItems() {

        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        OrderItemDomain item1 =
                new OrderItemDomain(ProductId.of(UUID.randomUUID()), "Product 1", Money.of(new BigDecimal("10.00")), 1);

        OrderItemDomain item2 =
                new OrderItemDomain(ProductId.of(UUID.randomUUID()), "Product 2", Money.of(new BigDecimal("25.50")), 3);

        OrderDomain orderDomain = new OrderDomain(
                OrderId.of(orderId),
                UserId.of(userId),
                List.of(item1, item2),
                Money.of(new BigDecimal("86.50")),
                OrderDomain.OrderStatus.PAID,
                createdAt);

        OrderJpaEntity orderEntity = orderEntityMapper.toEntity(orderDomain);

        assertThat(orderEntity.getItems()).hasSize(2);

        OrderItemJpaEntity jpaItem1 = orderEntity.getItems().get(0);
        assertThat(jpaItem1.getProductName()).isEqualTo("Product 1");
        assertThat(jpaItem1.getUnitPrice()).isEqualTo(new BigDecimal("10.00"));
        assertThat(jpaItem1.getQuantity()).isEqualTo(1);
        assertThat(jpaItem1.getSubtotal()).isEqualTo(new BigDecimal("10.00"));

        OrderItemJpaEntity jpaItem2 = orderEntity.getItems().get(1);
        assertThat(jpaItem2.getProductName()).isEqualTo("Product 2");
        assertThat(jpaItem2.getUnitPrice()).isEqualTo(new BigDecimal("25.50"));
        assertThat(jpaItem2.getQuantity()).isEqualTo(3);
        assertThat(jpaItem2.getSubtotal()).isEqualTo(new BigDecimal("76.50"));
    }

    @Test
    void shouldHandleMinimalOrder() {

        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        OrderItemDomain itemDomain =
                new OrderItemDomain(ProductId.of(productId), "Minimal Product", Money.of(new BigDecimal("0.01")), 1);

        OrderDomain orderDomain = new OrderDomain(
                OrderId.of(orderId),
                UserId.of(userId),
                List.of(itemDomain),
                Money.of(new BigDecimal("0.01")),
                OrderDomain.OrderStatus.PENDING,
                createdAt);

        OrderJpaEntity orderEntity = orderEntityMapper.toEntity(orderDomain);

        assertThat(orderEntity.getItems()).hasSize(1);
        assertThat(orderEntity.getTotalAmount()).isEqualTo(new BigDecimal("0.01"));
    }

    @Test
    void shouldMapCancelledOrderWithMinimalItem() {

        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        OrderItemJpaEntity itemEntity = new OrderItemJpaEntity(
                null, productId.toString(), "Cancelled Product", new BigDecimal("0.01"), 1, new BigDecimal("0.01"));
        itemEntity.setId(UUID.randomUUID().toString());

        OrderJpaEntity orderEntity = new OrderJpaEntity(
                orderId.toString(),
                userId.toString(),
                List.of(itemEntity),
                new BigDecimal("0.01"),
                OrderJpaEntity.OrderStatus.CANCELLED,
                createdAt);

        itemEntity.setOrderId(orderEntity.getId());

        OrderDomain orderDomain = orderEntityMapper.toDomain(orderEntity);

        assertThat(orderDomain.getTotalAmount().value()).isEqualTo(new BigDecimal("0.01"));
        assertThat(orderDomain.getItems()).hasSize(1);
        assertThat(orderDomain.getStatus()).isEqualTo(OrderDomain.OrderStatus.CANCELLED);
    }
}
