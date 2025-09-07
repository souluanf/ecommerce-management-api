package dev.luanfernandes.adapter.in.web.adapter.order;

import dev.luanfernandes.adapter.in.web.port.order.GetOrderPort;
import dev.luanfernandes.application.usecase.order.FindOrderByIdUseCase;
import dev.luanfernandes.domain.dto.OrderResponse;
import dev.luanfernandes.domain.exception.OrderNotFoundException;
import dev.luanfernandes.domain.valueobject.OrderId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GetOrderAdapter implements GetOrderPort {

    private final FindOrderByIdUseCase findOrderByIdUseCase;

    @Override
    public ResponseEntity<OrderResponse> getOrder(UUID id) {
        log.info("Finding order with ID: {}", id);

        var orderId = OrderId.of(id);
        var orderOpt = findOrderByIdUseCase.execute(orderId);
        var order = orderOpt.orElseThrow(() -> new OrderNotFoundException(id));

        log.info("Order found with ID: {}", id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}
