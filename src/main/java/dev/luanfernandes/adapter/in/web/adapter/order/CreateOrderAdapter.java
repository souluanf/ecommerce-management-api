package dev.luanfernandes.adapter.in.web.adapter.order;

import dev.luanfernandes.adapter.in.web.port.order.CreateOrderPort;
import dev.luanfernandes.application.usecase.order.CreateOrderUseCase;
import dev.luanfernandes.domain.dto.CreateOrderRequest;
import dev.luanfernandes.domain.dto.OrderResponse;
import dev.luanfernandes.domain.dto.command.CreateOrderCommand;
import dev.luanfernandes.domain.dto.command.OrderItemCommand;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CreateOrderAdapter implements CreateOrderPort {

    private final CreateOrderUseCase createOrderUseCase;

    @Override
    public ResponseEntity<OrderResponse> createOrder(CreateOrderRequest request) {
        log.info("Creating order with {} items", request.items().size());

        var userId = UserId.of(UUID.fromString(request.userId()));
        var orderItems = request.items().stream()
                .map(item -> new OrderItemCommand(ProductId.of(UUID.fromString(item.productId())), item.quantity()))
                .toList();

        var command = new CreateOrderCommand(userId, orderItems);

        var order = createOrderUseCase.execute(command);

        log.info("Order created successfully with ID: {}", order.getId().value());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }
}
