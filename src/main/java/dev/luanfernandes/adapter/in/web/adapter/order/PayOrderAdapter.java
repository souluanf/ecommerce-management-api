package dev.luanfernandes.adapter.in.web.adapter.order;

import dev.luanfernandes.adapter.in.web.port.order.PayOrderPort;
import dev.luanfernandes.application.usecase.order.PayOrderUseCase;
import dev.luanfernandes.domain.dto.OrderResponse;
import dev.luanfernandes.domain.valueobject.OrderId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PayOrderAdapter implements PayOrderPort {

    private final PayOrderUseCase payOrderUseCase;

    @Override
    public ResponseEntity<OrderResponse> payOrder(UUID orderId) {
        log.info("Processing payment for order: {}", orderId);

        var orderIdValue = OrderId.of(orderId);
        var order = payOrderUseCase.execute(orderIdValue);

        log.info("Payment processed successfully for order: {}", orderId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}
