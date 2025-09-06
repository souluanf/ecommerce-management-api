package dev.luanfernandes.domain.dto;

import java.util.List;

public record CreateOrderRequest(String userId, List<OrderItemRequest> items) {}
