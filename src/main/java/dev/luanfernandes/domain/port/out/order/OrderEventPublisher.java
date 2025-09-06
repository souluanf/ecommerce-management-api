package dev.luanfernandes.domain.port.out.order;

import dev.luanfernandes.domain.entity.OrderDomain;

public interface OrderEventPublisher {

    void publishOrderPaid(OrderDomain order);
}
