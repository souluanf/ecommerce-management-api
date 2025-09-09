package dev.luanfernandes.application.usecase.order;

import dev.luanfernandes.domain.exception.OrderNotFoundException;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import dev.luanfernandes.domain.valueobject.OrderId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class CancelOrderUseCase {

    private final OrderRepository orderRepository;

    public void cancel(OrderId orderId) {
        log.info("CancelOrder: Attempting to cancel order ID: {}", orderId.value());
        var order = orderRepository.findById(orderId).orElseThrow(() -> {
            log.error("CancelOrder: Order not found - ID: {}", orderId.value());
            return new OrderNotFoundException(orderId.value());
        });
        var cancelledOrder = order.cancel();
        orderRepository.save(cancelledOrder);
        log.info("CancelOrder: Order cancelled and saved successfully - ID: {}", orderId.value());
    }
}
