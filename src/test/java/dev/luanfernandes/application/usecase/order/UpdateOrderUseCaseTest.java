package dev.luanfernandes.application.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.dto.command.OrderItemCommand;
import dev.luanfernandes.domain.dto.command.UpdateOrderCommand;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.InvalidOrderStateException;
import dev.luanfernandes.domain.exception.ProductNotFoundException;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import dev.luanfernandes.domain.port.out.product.ProductRepository;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for UpdateOrderUseCase")
class UpdateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateOrderUseCase updateOrderUseCase;

    @Test
    @DisplayName("Should update order successfully when order exists and is pending")
    void shouldUpdateOrder_WhenOrderExistsAndIsPending() {
        OrderId orderId = OrderId.generate();
        ProductId productId = ProductId.generate();

        OrderDomain existingOrder = createOrder(orderId, OrderDomain.OrderStatus.PENDING);
        ProductDomain product = createProduct(productId, "Updated Product", new BigDecimal("149.99"));
        OrderItemCommand itemCommand = new OrderItemCommand(productId, 3);
        UpdateOrderCommand command = new UpdateOrderCommand(orderId, List.of(itemCommand));

        OrderDomain updatedOrder = createUpdatedOrder(orderId, product, 3);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(updatedOrder);

        Optional<OrderDomain> result = updateOrderUseCase.update(command);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(orderId);
        assertThat(result.get().getItems()).hasSize(1);
        assertThat(result.get().getTotalAmount()).isEqualTo(new Money(new BigDecimal("449.97")));
        assertThat(result.get().getStatus()).isEqualTo(OrderDomain.OrderStatus.PENDING);

        verify(orderRepository).findById(orderId);
        verify(productRepository).findById(productId);
        verify(orderRepository).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("Should return empty optional when order does not exist")
    void shouldReturnEmpty_WhenOrderDoesNotExist() {
        OrderId orderId = OrderId.generate();
        ProductId productId = ProductId.generate();
        OrderItemCommand itemCommand = new OrderItemCommand(productId, 2);
        UpdateOrderCommand command = new UpdateOrderCommand(orderId, List.of(itemCommand));

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Optional<OrderDomain> result = updateOrderUseCase.update(command);

        assertThat(result).isEmpty();

        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should throw InvalidOrderStateException when order is not pending")
    void shouldThrowInvalidOrderStateException_WhenOrderIsNotPending() {
        OrderId orderId = OrderId.generate();
        ProductId productId = ProductId.generate();

        OrderDomain paidOrder = createOrder(orderId, OrderDomain.OrderStatus.PAID);
        OrderItemCommand itemCommand = new OrderItemCommand(productId, 2);
        UpdateOrderCommand command = new UpdateOrderCommand(orderId, List.of(itemCommand));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(paidOrder));

        assertThatThrownBy(() -> updateOrderUseCase.update(command))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessage("Only pending orders can be updated");

        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product does not exist")
    void shouldThrowProductNotFoundException_WhenProductDoesNotExist() {
        OrderId orderId = OrderId.generate();
        ProductId productId = ProductId.generate();

        OrderDomain existingOrder = createOrder(orderId, OrderDomain.OrderStatus.PENDING);
        OrderItemCommand itemCommand = new OrderItemCommand(productId, 2);
        UpdateOrderCommand command = new UpdateOrderCommand(orderId, List.of(itemCommand));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateOrderUseCase.update(command)).isInstanceOf(ProductNotFoundException.class);

        verify(orderRepository).findById(orderId);
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should update order with multiple items")
    void shouldUpdateOrder_WithMultipleItems() {
        OrderId orderId = OrderId.generate();
        ProductId productId1 = ProductId.generate();
        ProductId productId2 = ProductId.generate();

        OrderDomain existingOrder = createOrder(orderId, OrderDomain.OrderStatus.PENDING);
        ProductDomain product1 = createProduct(productId1, "Product 1", new BigDecimal("99.99"));
        ProductDomain product2 = createProduct(productId2, "Product 2", new BigDecimal("49.99"));

        OrderItemCommand item1 = new OrderItemCommand(productId1, 2);
        OrderItemCommand item2 = new OrderItemCommand(productId2, 3);
        UpdateOrderCommand command = new UpdateOrderCommand(orderId, Arrays.asList(item1, item2));

        OrderDomain updatedOrder = createMultiItemOrder(orderId, product1, product2);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(productRepository.findById(productId1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(productId2)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(updatedOrder);

        Optional<OrderDomain> result = updateOrderUseCase.update(command);

        assertThat(result).isPresent();
        assertThat(result.get().getItems()).hasSize(2);
        assertThat(result.get().getStatus()).isEqualTo(OrderDomain.OrderStatus.PENDING);

        verify(orderRepository).findById(orderId);
        verify(productRepository).findById(productId1);
        verify(productRepository).findById(productId2);
        verify(orderRepository).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating order with empty items list")
    void shouldThrowIllegalArgumentException_WhenUpdatingWithEmptyItemsList() {
        OrderId orderId = OrderId.generate();

        OrderDomain existingOrder = createOrder(orderId, OrderDomain.OrderStatus.PENDING);
        UpdateOrderCommand command = new UpdateOrderCommand(orderId, List.of());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        assertThatThrownBy(() -> updateOrderUseCase.update(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order must have at least one item");

        verify(orderRepository).findById(orderId);
    }

    private OrderDomain createOrder(OrderId orderId, OrderDomain.OrderStatus status) {
        OrderItemDomain orderItem =
                new OrderItemDomain(ProductId.generate(), "Original Product", new Money(new BigDecimal("99.99")), 1);
        return new OrderDomain(
                orderId,
                UserId.generate(),
                List.of(orderItem),
                new Money(new BigDecimal("99.99")),
                status,
                LocalDateTime.now());
    }

    private OrderDomain createUpdatedOrder(OrderId orderId, ProductDomain product, int quantity) {
        OrderItemDomain orderItem =
                new OrderItemDomain(product.getId(), product.getName(), product.getPrice(), quantity);
        Money totalAmount = new Money(product.getPrice().value().multiply(BigDecimal.valueOf(quantity)));
        return new OrderDomain(
                orderId,
                UserId.generate(),
                List.of(orderItem),
                totalAmount,
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());
    }

    private OrderDomain createMultiItemOrder(OrderId orderId, ProductDomain product1, ProductDomain product2) {
        OrderItemDomain orderItem1 = new OrderItemDomain(product1.getId(), product1.getName(), product1.getPrice(), 2);
        OrderItemDomain orderItem2 = new OrderItemDomain(product2.getId(), product2.getName(), product2.getPrice(), 3);
        Money totalAmount = new Money(product1.getPrice().value().multiply(BigDecimal.valueOf(2)))
                .add(new Money(product2.getPrice().value().multiply(BigDecimal.valueOf(3))));
        return new OrderDomain(
                orderId,
                UserId.generate(),
                List.of(orderItem1, orderItem2),
                totalAmount,
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());
    }

    private ProductDomain createProduct(ProductId productId, String name, BigDecimal price) {
        return new ProductDomain(
                productId, name, "Test Description", new Money(price), "ELECTRONICS", 100, LocalDateTime.now());
    }
}
