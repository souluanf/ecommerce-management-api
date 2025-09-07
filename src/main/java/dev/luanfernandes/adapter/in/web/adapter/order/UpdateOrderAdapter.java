package dev.luanfernandes.adapter.in.web.adapter.order;

import dev.luanfernandes.adapter.in.web.port.order.UpdateOrderPort;
import dev.luanfernandes.application.usecase.order.UpdateOrderUseCase;
import dev.luanfernandes.domain.dto.OrderResponse;
import dev.luanfernandes.domain.dto.UpdateOrderRequest;
import dev.luanfernandes.domain.dto.command.OrderItemCommand;
import dev.luanfernandes.domain.dto.command.UpdateOrderCommand;
import dev.luanfernandes.domain.exception.OrderNotFoundException;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UpdateOrderAdapter implements UpdateOrderPort {

    private final UpdateOrderUseCase updateOrderUseCase;

    @Override
    public ResponseEntity<OrderResponse> updateOrder(UUID id, UpdateOrderRequest request) {
        log.info("Updating order: {} with {} items", id, request.items().size());

        var orderId = OrderId.of(id);
        var orderItems = request.items().stream()
                .map(item -> new OrderItemCommand(ProductId.of(UUID.fromString(item.productId())), item.quantity()))
                .toList();

        var command = new UpdateOrderCommand(orderId, orderItems);
        var orderOpt = updateOrderUseCase.update(command);
        var order = orderOpt.orElseThrow(() -> new OrderNotFoundException(id));

        log.info("Order updated successfully: {}", id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}
