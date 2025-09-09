package dev.luanfernandes.application.usecase.order;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.exception.OrderNotFoundException;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for CancelOrderUseCase")
class CancelOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CancelOrderUseCase cancelOrderUseCase;

    @Test
    @DisplayName("Should cancel order successfully when order exists and is pending")
    void shouldCancelOrder_WhenOrderExistsAndIsPending() {
        OrderId orderId = OrderId.generate();
        OrderDomain pendingOrder = createOrder(orderId, OrderDomain.OrderStatus.PENDING);
        OrderDomain cancelledOrder = createOrder(orderId, OrderDomain.OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(cancelledOrder);

        cancelOrderUseCase.cancel(orderId);

        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order does not exist")
    void shouldThrowOrderNotFoundException_WhenOrderDoesNotExist() {
        OrderId orderId = OrderId.generate();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cancelOrderUseCase.cancel(orderId)).isInstanceOf(OrderNotFoundException.class);

        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to cancel already paid order")
    void shouldThrowIllegalStateException_WhenTryingToCancelPaidOrder() {
        OrderId orderId = OrderId.generate();
        OrderDomain paidOrder = createOrder(orderId, OrderDomain.OrderStatus.PAID);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(paidOrder));

        assertThatThrownBy(() -> cancelOrderUseCase.cancel(orderId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Paid orders cannot be cancelled");

        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should handle cancellation of already cancelled order")
    void shouldHandleCancellationOfAlreadyCancelledOrder() {
        OrderId orderId = OrderId.generate();
        OrderDomain cancelledOrder = createOrder(orderId, OrderDomain.OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(cancelledOrder));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(cancelledOrder);

        cancelOrderUseCase.cancel(orderId);

        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("Should successfully cancel order with multiple items")
    void shouldSuccessfullyCancelOrder_WithMultipleItems() {
        OrderId orderId = OrderId.generate();
        OrderDomain pendingOrder = createOrderWithMultipleItems(orderId, OrderDomain.OrderStatus.PENDING);
        OrderDomain cancelledOrder = createOrderWithMultipleItems(orderId, OrderDomain.OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(cancelledOrder);

        cancelOrderUseCase.cancel(orderId);

        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(OrderDomain.class));
    }

    private OrderDomain createOrder(OrderId orderId, OrderDomain.OrderStatus status) {
        OrderItemDomain orderItem =
                new OrderItemDomain(ProductId.generate(), "Test Product", new Money(new BigDecimal("99.99")), 1);
        return new OrderDomain(
                orderId,
                UserId.generate(),
                List.of(orderItem),
                new Money(new BigDecimal("99.99")),
                status,
                LocalDateTime.now());
    }

    private OrderDomain createOrderWithMultipleItems(OrderId orderId, OrderDomain.OrderStatus status) {
        OrderItemDomain orderItem1 =
                new OrderItemDomain(ProductId.generate(), "Test Product 1", new Money(new BigDecimal("99.99")), 2);
        OrderItemDomain orderItem2 =
                new OrderItemDomain(ProductId.generate(), "Test Product 2", new Money(new BigDecimal("49.99")), 1);
        Money totalAmount = new Money(new BigDecimal("199.98")).add(new Money(new BigDecimal("49.99")));
        return new OrderDomain(
                orderId, UserId.generate(), List.of(orderItem1, orderItem2), totalAmount, status, LocalDateTime.now());
    }
}
