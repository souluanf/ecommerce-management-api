package dev.luanfernandes.domain.dto.command;

import dev.luanfernandes.domain.valueobject.OrderId;
import java.util.List;

public record UpdateOrderCommand(OrderId orderId, List<OrderItemCommand> items) {}
