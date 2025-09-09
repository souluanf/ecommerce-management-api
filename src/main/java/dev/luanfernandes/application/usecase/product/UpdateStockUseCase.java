package dev.luanfernandes.application.usecase.product;

import dev.luanfernandes.adapter.out.messaging.event.OrderItemEvent;
import dev.luanfernandes.adapter.out.messaging.event.StockUpdateEvent;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.ProductNotFoundException;
import dev.luanfernandes.domain.exception.StockUpdateException;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateStockUseCase {

    private final ProductRepository productRepository;

    @Transactional
    public List<StockUpdateEvent> updateStockFromOrder(String orderId, List<OrderItemEvent> items) {
        log.info("StockUpdate: Processing stock updates for order: {} with {} items", orderId, items.size());

        List<StockUpdateEvent> updates = new ArrayList<>();

        for (OrderItemEvent item : items) {
            try {
                ProductDomain product = productRepository
                        .findById(ProductId.of(item.productId()))
                        .orElseThrow(() -> {
                            log.error("StockUpdate: Product not found - ID: {}", item.productId());
                            return new ProductNotFoundException(item.productId());
                        });

                int previousStock = product.getStockQuantity();

                log.info(
                        "StockUpdate: Confirmed stock deduction - product: '{}', previous: {}, deducted: {}",
                        product.getName(),
                        previousStock,
                        item.quantity());

                StockUpdateEvent updateEvent =
                        StockUpdateEvent.create(item.productId(), previousStock, previousStock, item.quantity());

                updates.add(updateEvent);

            } catch (Exception e) {
                log.error(
                        "StockUpdate: Failed to update stock for product: {} - error: {}",
                        item.productId(),
                        e.getMessage(),
                        e);
                throw new StockUpdateException(item.productId(), e);
            }
        }

        log.info("StockUpdate: Completed stock updates for order: {} - {} items processed", orderId, updates.size());

        return updates;
    }
}
