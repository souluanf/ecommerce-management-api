package dev.luanfernandes.application.usecase.order;

import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import dev.luanfernandes.domain.valueobject.OrderId;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FindOrderByIdUseCase {

    private static final Logger log = LoggerFactory.getLogger(FindOrderByIdUseCase.class);

    private final OrderRepository orderRepository;

    public FindOrderByIdUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<OrderDomain> execute(OrderId orderId) {
        log.info("FindOrderById: Searching for order with ID: {}", orderId.value());

        var result = orderRepository.findById(orderId);

        if (result.isPresent()) {
            var order = result.get();
            log.info(
                    "FindOrderById: Order found - ID: {}, status: {}, total: {}",
                    orderId.value(),
                    order.getStatus(),
                    order.getTotalAmount().value());
        } else {
            log.warn("FindOrderById: Order NOT found with ID: {}", orderId.value());
        }

        return result;
    }
}
