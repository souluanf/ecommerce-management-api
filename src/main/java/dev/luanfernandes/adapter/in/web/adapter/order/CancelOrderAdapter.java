package dev.luanfernandes.adapter.in.web.adapter.order;

import dev.luanfernandes.adapter.in.web.port.order.CancelOrderPort;
import dev.luanfernandes.application.usecase.order.CancelOrderUseCase;
import dev.luanfernandes.domain.valueobject.OrderId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CancelOrderAdapter implements CancelOrderPort {

    private final CancelOrderUseCase cancelOrderUseCase;

    @Override
    public ResponseEntity<Void> cancelOrder(UUID id) {
        log.info("Cancelling order: {}", id);

        var orderId = OrderId.of(id);
        cancelOrderUseCase.cancel(orderId);

        log.info("Order cancelled successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
