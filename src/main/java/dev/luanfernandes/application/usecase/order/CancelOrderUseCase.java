package dev.luanfernandes.application.usecase.order;

import dev.luanfernandes.domain.exception.OrderNotFoundException;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import dev.luanfernandes.domain.valueobject.OrderId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CancelOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(CancelOrderUseCase.class);

    private final OrderRepository orderRepository;

    public CancelOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

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
