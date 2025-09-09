package dev.luanfernandes.application.usecase.order;

import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.exception.InvalidOrderStateException;
import dev.luanfernandes.domain.exception.OrderNotFoundException;
import dev.luanfernandes.domain.port.out.order.OrderEventPublisher;
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
public class PayOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    public OrderDomain execute(OrderId orderId) {
        log.info("PayOrder: Processing payment for order ID: {}", orderId.value());

        OrderDomain order = orderRepository.findById(orderId).orElseThrow(() -> {
            log.error("PayOrder: Order not found - ID: {}", orderId.value());
            return new OrderNotFoundException(orderId.value());
        });

        if (!order.isPending()) {
            log.warn("⚠️ PayOrder: Order is not pending - ID: {}, status: {}", orderId.value(), order.getStatus());
            throw new InvalidOrderStateException("Order is not pending: " + orderId);
        }

        OrderDomain paidOrder = order.markAsPaid();
        OrderDomain savedOrder = orderRepository.save(paidOrder);
        log.info(
                "PayOrder: Order marked as paid and saved - ID: {}",
                savedOrder.getId().value());

        try {
            orderEventPublisher.publishOrderPaid(savedOrder);
            log.info(
                    "PayOrder: Order paid event published - ID: {}",
                    savedOrder.getId().value());
        } catch (Exception e) {
            log.warn(
                    "PayOrder: Failed to publish order paid event - ID: {}, error: {}",
                    savedOrder.getId().value(),
                    e.getMessage());
        }

        return savedOrder;
    }
}
