package dev.luanfernandes.domain.entity;

import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderDomain {

    private final OrderId id;
    private final UserId userId;
    private final List<OrderItemDomain> items;
    private final Money totalAmount;
    private final OrderStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public OrderDomain(
            OrderId id,
            UserId userId,
            List<OrderItemDomain> items,
            Money totalAmount,
            OrderStatus status,
            LocalDateTime createdAt) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        this.id = id;
        this.userId = userId;
        this.items = List.copyOf(items);
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public OrderDomain markAsPaid() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be paid");
        }
        return new OrderDomain(id, userId, items, totalAmount, OrderStatus.PAID, createdAt);
    }

    public OrderDomain cancel() {
        if (status == OrderStatus.PAID) {
            throw new IllegalStateException("Paid orders cannot be cancelled");
        }
        return new OrderDomain(id, userId, items, totalAmount, OrderStatus.CANCELLED, createdAt);
    }

    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }

    public boolean isPaid() {
        return status == OrderStatus.PAID;
    }

    public boolean isCancelled() {
        return status == OrderStatus.CANCELLED;
    }

    public enum OrderStatus {
        PENDING,
        PAID,
        CANCELLED
    }
}
