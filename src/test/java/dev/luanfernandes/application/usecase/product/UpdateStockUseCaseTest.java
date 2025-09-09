package dev.luanfernandes.application.usecase.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.adapter.out.messaging.event.OrderItemEvent;
import dev.luanfernandes.adapter.out.messaging.event.StockUpdateEvent;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.ProductNotFoundException;
import dev.luanfernandes.domain.exception.StockUpdateException;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for UpdateStockUseCase")
class UpdateStockUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateStockUseCase updateStockUseCase;

    @Test
    @DisplayName("Should update stock successfully for single item")
    void shouldUpdateStock_WhenSingleItemProvided() {
        String orderId = "order-123";
        String productId = "product-456";
        ProductDomain product = createProduct(productId, 100);
        OrderItemEvent orderItem = new OrderItemEvent(productId, "Test Product", 5, new BigDecimal("99.99"));

        when(productRepository.findById(ProductId.of(productId))).thenReturn(Optional.of(product));

        List<StockUpdateEvent> result = updateStockUseCase.updateStockFromOrder(orderId, List.of(orderItem));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).productId()).isEqualTo(productId);
        assertThat(result.get(0).previousStock()).isEqualTo(100);
        assertThat(result.get(0).newStock()).isEqualTo(100);
        assertThat(result.get(0).quantityReduced()).isEqualTo(5);

        verify(productRepository).findById(ProductId.of(productId));
    }

    @Test
    @DisplayName("Should update stock for multiple items")
    void shouldUpdateStock_WhenMultipleItemsProvided() {
        String orderId = "order-123";
        String productId1 = "product-1";
        String productId2 = "product-2";

        ProductDomain product1 = createProduct(productId1, 50);
        ProductDomain product2 = createProduct(productId2, 30);

        OrderItemEvent item1 = new OrderItemEvent(productId1, "Product 1", 3, new BigDecimal("99.99"));
        OrderItemEvent item2 = new OrderItemEvent(productId2, "Product 2", 2, new BigDecimal("49.99"));

        when(productRepository.findById(ProductId.of(productId1))).thenReturn(Optional.of(product1));
        when(productRepository.findById(ProductId.of(productId2))).thenReturn(Optional.of(product2));

        List<StockUpdateEvent> result = updateStockUseCase.updateStockFromOrder(orderId, Arrays.asList(item1, item2));

        assertThat(result).hasSize(2);

        StockUpdateEvent update1 = result.get(0);
        assertThat(update1.productId()).isEqualTo(productId1);
        assertThat(update1.previousStock()).isEqualTo(50);
        assertThat(update1.quantityReduced()).isEqualTo(3);

        StockUpdateEvent update2 = result.get(1);
        assertThat(update2.productId()).isEqualTo(productId2);
        assertThat(update2.previousStock()).isEqualTo(30);
        assertThat(update2.quantityReduced()).isEqualTo(2);

        verify(productRepository).findById(ProductId.of(productId1));
        verify(productRepository).findById(ProductId.of(productId2));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found")
    void shouldThrowProductNotFoundException_WhenProductNotFound() {
        String orderId = "order-123";
        String productId = "nonexistent-product";
        OrderItemEvent orderItem = new OrderItemEvent(productId, "Test Product", 1, new BigDecimal("99.99"));
        List<OrderItemEvent> orderItems = List.of(orderItem);

        when(productRepository.findById(ProductId.of(productId))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateStockUseCase.updateStockFromOrder(orderId, orderItems))
                .isInstanceOf(StockUpdateException.class)
                .hasCauseInstanceOf(ProductNotFoundException.class);

        verify(productRepository).findById(ProductId.of(productId));
    }

    @Test
    @DisplayName("Should throw StockUpdateException when repository throws exception")
    void shouldThrowStockUpdateException_WhenRepositoryThrowsException() {
        String orderId = "order-123";
        String productId = "product-456";
        OrderItemEvent orderItem = new OrderItemEvent(productId, "Test Product", 5, new BigDecimal("99.99"));
        List<OrderItemEvent> orderItems = List.of(orderItem);

        when(productRepository.findById(ProductId.of(productId))).thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> updateStockUseCase.updateStockFromOrder(orderId, orderItems))
                .isInstanceOf(StockUpdateException.class);

        verify(productRepository).findById(ProductId.of(productId));
    }

    @Test
    @DisplayName("Should handle empty order items list")
    void shouldHandleEmptyOrderItemsList() {
        String orderId = "order-123";

        List<StockUpdateEvent> result = updateStockUseCase.updateStockFromOrder(orderId, Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle partial failures in multi-item order")
    void shouldHandlePartialFailures_InMultiItemOrder() {
        String orderId = "order-123";
        String existingProductId = "existing-product";
        String nonExistentProductId = "nonexistent-product";

        ProductDomain existingProduct = createProduct(existingProductId, 20);
        OrderItemEvent existingItem =
                new OrderItemEvent(existingProductId, "Existing Product", 2, new BigDecimal("99.99"));
        OrderItemEvent nonExistentItem =
                new OrderItemEvent(nonExistentProductId, "Non Existent Product", 1, new BigDecimal("49.99"));

        when(productRepository.findById(ProductId.of(existingProductId))).thenReturn(Optional.of(existingProduct));
        when(productRepository.findById(ProductId.of(nonExistentProductId))).thenReturn(Optional.empty());

        List<OrderItemEvent> orderItems = Arrays.asList(existingItem, nonExistentItem);

        assertThatThrownBy(() -> updateStockUseCase.updateStockFromOrder(orderId, orderItems))
                .isInstanceOf(StockUpdateException.class);

        verify(productRepository).findById(ProductId.of(existingProductId));
        verify(productRepository).findById(ProductId.of(nonExistentProductId));
    }

    @Test
    @DisplayName("Should update stock with zero quantity")
    void shouldUpdateStock_WithZeroQuantity() {
        String orderId = "order-123";
        String productId = "product-456";
        ProductDomain product = createProduct(productId, 10);
        OrderItemEvent orderItem = new OrderItemEvent(productId, "Test Product", 0, new BigDecimal("99.99"));

        when(productRepository.findById(ProductId.of(productId))).thenReturn(Optional.of(product));

        List<StockUpdateEvent> result = updateStockUseCase.updateStockFromOrder(orderId, List.of(orderItem));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).quantityReduced()).isZero();
        assertThat(result.get(0).previousStock()).isEqualTo(10);

        verify(productRepository).findById(ProductId.of(productId));
    }

    private ProductDomain createProduct(String productIdStr, int stockQuantity) {
        return new ProductDomain(
                ProductId.of(productIdStr),
                "Test Product " + productIdStr,
                "Test Description",
                new Money(new BigDecimal("99.99")),
                "ELECTRONICS",
                stockQuantity,
                LocalDateTime.now());
    }
}
