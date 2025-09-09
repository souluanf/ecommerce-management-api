package dev.luanfernandes.application.usecase.order;

import dev.luanfernandes.domain.dto.command.CreateOrderCommand;
import dev.luanfernandes.domain.dto.command.OrderItemCommand;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.ProductNotFoundException;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderDomain execute(CreateOrderCommand command) {
        log.info(
                "CreateOrder: Creating new order - userId: {}, items count: {}",
                command.userId(),
                command.items().size());

        List<OrderItemDomain> orderItems = new ArrayList<>();
        List<ProductDomain> reservedProducts = new ArrayList<>();
        Money totalAmount = Money.zero();
        boolean hasStockIssue = false;
        String stockIssueReason = "";

        for (OrderItemCommand itemCommand : command.items()) {
            log.debug(
                    "CreateOrder: Processing item - productId: {}, quantity: {}",
                    itemCommand.productId().value(),
                    itemCommand.quantity());

            ProductDomain product = productRepository
                    .findById(itemCommand.productId())
                    .orElseThrow(() -> {
                        log.error(
                                "CreateOrder: Product not found - ID: {}",
                                itemCommand.productId().value());
                        return new ProductNotFoundException(
                                itemCommand.productId().value());
                    });

            log.debug(
                    "CreateOrder: Found product '{}' - stock: {}, requested: {}",
                    product.getName(),
                    product.getStockQuantity(),
                    itemCommand.quantity());

            if (!product.hasEnoughStock(itemCommand.quantity())) {
                log.warn(
                        "CreateOrder: Insufficient stock - product: '{}', available: {}, requested: {}",
                        product.getName(),
                        product.getStockQuantity(),
                        itemCommand.quantity());
                hasStockIssue = true;
                stockIssueReason = "Insufficient stock for product: " + product.getName();
            } else {
                try {
                    ProductDomain updatedProduct = product.reduceStock(itemCommand.quantity());
                    productRepository.save(updatedProduct);
                    reservedProducts.add(updatedProduct);
                    log.info(
                            "CreateOrder: Stock reserved - product: '{}', reserved: {}, remaining: {}",
                            product.getName(),
                            itemCommand.quantity(),
                            updatedProduct.getStockQuantity());
                } catch (IllegalStateException e) {
                    log.warn(
                            "CreateOrder: Failed to reserve stock - product: '{}', error: {}",
                            product.getName(),
                            e.getMessage());
                    hasStockIssue = true;
                    stockIssueReason = "Failed to reserve stock for product: " + product.getName();
                }
            }

            OrderItemDomain orderItem =
                    new OrderItemDomain(product.getId(), product.getName(), product.getPrice(), itemCommand.quantity());

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());

            log.debug(
                    "CreateOrder: Added item - subtotal: {}, running total: {}",
                    orderItem.getSubtotal().value(),
                    totalAmount.value());
        }

        if (hasStockIssue) {
            rollbackReservedStock(reservedProducts, command);
            return createCancelledOrder(command, orderItems, totalAmount, stockIssueReason);
        }

        OrderDomain order = new OrderDomain(
                OrderId.generate(),
                command.userId(),
                orderItems,
                totalAmount,
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());

        log.info(
                "CreateOrder: Order created - ID: {}, total: {}, items: {}",
                order.getId().value(),
                totalAmount.value(),
                orderItems.size());

        OrderDomain savedOrder = orderRepository.save(order);
        log.info(
                "CreateOrder: Order saved successfully - ID: {}",
                savedOrder.getId().value());
        return savedOrder;
    }

    private OrderDomain createCancelledOrder(
            CreateOrderCommand command, List<OrderItemDomain> processedItems, Money partialTotal, String reason) {
        log.info("CreateOrder: Creating cancelled order - userId: {}, reason: {}", command.userId(), reason);

        OrderDomain cancelledOrder = new OrderDomain(
                OrderId.generate(),
                command.userId(),
                processedItems,
                partialTotal,
                OrderDomain.OrderStatus.CANCELLED,
                LocalDateTime.now());

        OrderDomain savedOrder = orderRepository.save(cancelledOrder);
        log.info(
                "CreateOrder: Cancelled order saved - ID: {}",
                savedOrder.getId().value());

        return savedOrder;
    }

    private void rollbackReservedStock(List<ProductDomain> reservedProducts, CreateOrderCommand command) {
        for (ProductDomain reservedProduct : reservedProducts) {
            try {
                int reservedQuantity = command.items().stream()
                        .filter(item -> item.productId().equals(reservedProduct.getId()))
                        .mapToInt(OrderItemCommand::quantity)
                        .findFirst()
                        .orElse(0);
                if (reservedQuantity > 0) {
                    ProductDomain restoredProduct = new ProductDomain(
                            reservedProduct.getId(),
                            reservedProduct.getName(),
                            reservedProduct.getDescription(),
                            reservedProduct.getPrice(),
                            reservedProduct.getCategory(),
                            reservedProduct.getStockQuantity() + reservedQuantity,
                            reservedProduct.getCreatedAt());

                    productRepository.save(restoredProduct);
                    log.info(
                            "CreateOrder: Stock restored - product: '{}', restored: {}, new total: {}",
                            reservedProduct.getName(),
                            reservedQuantity,
                            restoredProduct.getStockQuantity());
                }
            } catch (Exception e) {
                log.error(
                        "CreateOrder: Failed to rollback stock for product '{}' - error: {}",
                        reservedProduct.getName(),
                        e.getMessage());
            }
        }
    }
}
