package dev.luanfernandes.domain.dto.command;

import dev.luanfernandes.domain.valueobject.UserId;
import java.util.List;

public record CreateOrderCommand(UserId userId, List<OrderItemCommand> items) {}
