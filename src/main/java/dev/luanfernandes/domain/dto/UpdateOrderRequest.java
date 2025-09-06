package dev.luanfernandes.domain.dto;

import java.util.List;

public record UpdateOrderRequest(List<OrderItemRequest> items) {}
