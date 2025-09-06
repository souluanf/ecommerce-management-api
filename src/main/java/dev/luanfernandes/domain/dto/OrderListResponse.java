package dev.luanfernandes.domain.dto;

import dev.luanfernandes.domain.entity.OrderDomain;
import java.util.List;

public record OrderListResponse(List<OrderResponse> orders) {
    public static OrderListResponse from(List<OrderDomain> orders) {
        var orderResponses = orders.stream().map(OrderResponse::from).toList();
        return new OrderListResponse(orderResponses);
    }
}
