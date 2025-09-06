package dev.luanfernandes.application.usecase.order;

import dev.luanfernandes.domain.dto.command.UpdateOrderCommand;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.exception.InvalidOrderStateException;
import dev.luanfernandes.domain.exception.ProductNotFoundException;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.valueobject.Money;
import java.math.BigDecimal;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateOrderUseCase.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public UpdateOrderUseCase(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Optional<OrderDomain> update(UpdateOrderCommand command) {
        log.info(
                "UpdateOrder: Updating order ID: {} with {} items",
                command.orderId().value(),
                command.items().size());

        return orderRepository
                .findById(command.orderId())
                .map(existingOrder -> {
                    if (!existingOrder.isPending()) {
                        log.warn(
                                "UpdateOrder: Cannot update non-pending order - ID: {}, status: {}",
                                command.orderId().value(),
                                existingOrder.getStatus());
                        throw new InvalidOrderStateException("Only pending orders can be updated");
                    }

                    var newItems = command.items().stream()
                            .map(itemCommand -> {
                                var product = productRepository
                                        .findById(itemCommand.productId())
                                        .orElseThrow(() -> {
                                            log.error(
                                                    "UpdateOrder: Product not found - ID: {}",
                                                    itemCommand.productId().value());
                                            return new ProductNotFoundException(
                                                    itemCommand.productId().value());
                                        });

                                return new OrderItemDomain(
                                        itemCommand.productId(),
                                        product.getName(),
                                        product.getPrice(),
                                        itemCommand.quantity());
                            })
                            .toList();

                    var newTotal = Money.of(newItems.stream()
                            .map(item -> item.getSubtotal().value())
                            .reduce(BigDecimal.ZERO, BigDecimal::add));

                    var updatedOrder = new OrderDomain(
                            existingOrder.getId(),
                            existingOrder.getUserId(),
                            newItems,
                            newTotal,
                            existingOrder.getStatus(),
                            existingOrder.getCreatedAt());

                    var savedOrder = orderRepository.save(updatedOrder);
                    log.info(
                            "UpdateOrder: Order updated successfully - ID: {}, new total: {}",
                            savedOrder.getId().value(),
                            savedOrder.getTotalAmount().value());

                    return savedOrder;
                })
                .or(() -> {
                    log.warn(
                            "UpdateOrder: Order not found for update - ID: {}",
                            command.orderId().value());
                    return Optional.empty();
                });
    }
}
