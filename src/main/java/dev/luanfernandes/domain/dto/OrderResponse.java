package dev.luanfernandes.domain.dto;

import dev.luanfernandes.domain.entity.OrderDomain;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String id,
        String userId,
        List<OrderItemResponse> items,
        BigDecimal totalAmount,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    public static OrderResponse from(OrderDomain order) {
        var itemResponses =
                order.getItems().stream().map(OrderItemResponse::from).toList();

        return new OrderResponse(
                order.getId().value(),
                order.getUserId().value(),
                itemResponses,
                order.getTotalAmount().value(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getUpdatedAt());
    }
}
