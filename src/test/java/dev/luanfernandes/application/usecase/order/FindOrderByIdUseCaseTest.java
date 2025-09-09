package dev.luanfernandes.application.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
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
@DisplayName("Tests for FindOrderByIdUseCase")
class FindOrderByIdUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private FindOrderByIdUseCase findOrderByIdUseCase;

    @Test
    @DisplayName("Should return order when order exists")
    void shouldReturnOrder_WhenOrderExists() {
        OrderId orderId = OrderId.generate();
        OrderDomain expectedOrder = createOrder(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(expectedOrder));

        Optional<OrderDomain> result = findOrderByIdUseCase.execute(orderId);

        assertThat(result).isPresent().hasValueSatisfying(order -> {
            assertThat(order).isEqualTo(expectedOrder);
            assertThat(order.getId()).isEqualTo(orderId);
            assertThat(order.getStatus()).isEqualTo(OrderDomain.OrderStatus.PENDING);
        });

        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should return empty optional when order not found")
    void shouldReturnEmpty_WhenOrderNotFound() {
        OrderId orderId = OrderId.generate();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Optional<OrderDomain> result = findOrderByIdUseCase.execute(orderId);

        assertThat(result).isEmpty();

        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should return cancelled order when order is cancelled")
    void shouldReturnCancelledOrder_WhenOrderIsCancelled() {
        OrderId orderId = OrderId.generate();
        OrderDomain cancelledOrder = createCancelledOrder(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(cancelledOrder));

        Optional<OrderDomain> result = findOrderByIdUseCase.execute(orderId);

        assertThat(result).isPresent().hasValueSatisfying(order -> {
            assertThat(order.getStatus()).isEqualTo(OrderDomain.OrderStatus.CANCELLED);
            assertThat(order.getId()).isEqualTo(orderId);
        });

        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should return paid order when order is paid")
    void shouldReturnPaidOrder_WhenOrderIsPaid() {
        OrderId orderId = OrderId.generate();
        OrderDomain paidOrder = createPaidOrder(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(paidOrder));

        Optional<OrderDomain> result = findOrderByIdUseCase.execute(orderId);

        assertThat(result).isPresent().hasValueSatisfying(order -> {
            assertThat(order.getStatus()).isEqualTo(OrderDomain.OrderStatus.PAID);
            assertThat(order.getId()).isEqualTo(orderId);
        });

        verify(orderRepository).findById(orderId);
    }

    private OrderDomain createOrder(OrderId orderId) {
        OrderItemDomain orderItem =
                new OrderItemDomain(ProductId.generate(), "Test Product", new Money(new BigDecimal("99.99")), 2);
        return new OrderDomain(
                orderId,
                UserId.generate(),
                List.of(orderItem),
                new Money(new BigDecimal("199.98")),
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());
    }

    private OrderDomain createCancelledOrder(OrderId orderId) {
        OrderItemDomain orderItem =
                new OrderItemDomain(ProductId.generate(), "Test Product", new Money(new BigDecimal("99.99")), 1);
        return new OrderDomain(
                orderId,
                UserId.generate(),
                List.of(orderItem),
                new Money(new BigDecimal("99.99")),
                OrderDomain.OrderStatus.CANCELLED,
                LocalDateTime.now());
    }

    private OrderDomain createPaidOrder(OrderId orderId) {
        OrderItemDomain orderItem =
                new OrderItemDomain(ProductId.generate(), "Test Product", new Money(new BigDecimal("149.99")), 3);
        return new OrderDomain(
                orderId,
                UserId.generate(),
                List.of(orderItem),
                new Money(new BigDecimal("449.97")),
                OrderDomain.OrderStatus.PAID,
                LocalDateTime.now());
    }
}
