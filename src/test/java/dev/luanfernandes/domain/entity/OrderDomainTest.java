package dev.luanfernandes.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for OrderDomain")
class OrderDomainTest {

    @Test
    @DisplayName("Should create order with valid data")
    void shouldCreateOrder_WithValidData() {
        OrderId id = OrderId.generate();
        UserId userId = UserId.generate();
        List<OrderItemDomain> items = createOrderItems();
        Money totalAmount = new Money(new BigDecimal("199.98"));
        OrderDomain.OrderStatus status = OrderDomain.OrderStatus.PENDING;
        LocalDateTime createdAt = LocalDateTime.now();

        OrderDomain order = new OrderDomain(id, userId, items, totalAmount, status, createdAt);

        assertThat(order).satisfies(o -> {
            assertThat(o.getId()).isEqualTo(id);
            assertThat(o.getUserId()).isEqualTo(userId);
            assertThat(o.getItems()).isEqualTo(items);
            assertThat(o.getTotalAmount()).isEqualTo(totalAmount);
            assertThat(o.getStatus()).isEqualTo(status);
            assertThat(o.getCreatedAt()).isEqualTo(createdAt);
        });
    }

    @Test
    @DisplayName("Should throw exception when creating order with empty items list")
    void shouldThrowException_WhenCreatingOrderWithEmptyItemsList() {
        OrderId orderId = OrderId.generate();
        UserId userId = UserId.generate();
        Money totalAmount = new Money(BigDecimal.ZERO);
        LocalDateTime createdAt = LocalDateTime.now();
        List<OrderItemDomain> emptyItems = Collections.emptyList();

        assertThatThrownBy(() -> new OrderDomain(
                        orderId, userId, emptyItems, totalAmount, OrderDomain.OrderStatus.PENDING, createdAt))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw exception when creating order with null items list")
    void shouldThrowException_WhenCreatingOrderWithNullItemsList() {
        OrderId orderId = OrderId.generate();
        UserId userId = UserId.generate();
        Money totalAmount = new Money(BigDecimal.ZERO);
        LocalDateTime createdAt = LocalDateTime.now();

        assertThatThrownBy(() ->
                        new OrderDomain(orderId, userId, null, totalAmount, OrderDomain.OrderStatus.PENDING, createdAt))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should get total amount correctly")
    void shouldGetTotalAmount_Correctly() {
        List<OrderItemDomain> items = Arrays.asList(
                new OrderItemDomain(ProductId.generate(), "Product 1", new Money(new BigDecimal("50.00")), 2),
                new OrderItemDomain(ProductId.generate(), "Product 2", new Money(new BigDecimal("30.00")), 1),
                new OrderItemDomain(ProductId.generate(), "Product 3", new Money(new BigDecimal("25.00")), 3));

        Money totalAmount = new Money(new BigDecimal("205.00"));
        OrderDomain order = new OrderDomain(
                OrderId.generate(),
                UserId.generate(),
                items,
                totalAmount,
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());

        assertThat(order.getTotalAmount()).isEqualTo(totalAmount);
    }

    @Test
    @DisplayName("Should get total amount with single item")
    void shouldGetTotalAmount_WithSingleItem() {
        List<OrderItemDomain> items = Collections.singletonList(
                new OrderItemDomain(ProductId.generate(), "Single Product", new Money(new BigDecimal("99.99")), 1));

        Money totalAmount = new Money(new BigDecimal("99.99"));
        OrderDomain order = new OrderDomain(
                OrderId.generate(),
                UserId.generate(),
                items,
                totalAmount,
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());

        assertThat(order.getTotalAmount()).isEqualTo(totalAmount);
    }

    @Test
    @DisplayName("Should be able to cancel when status is PENDING")
    void shouldBeAbleToCancel_WhenStatusIsPending() {
        OrderDomain order = createOrder(createOrderItems(), OrderDomain.OrderStatus.PENDING);

        assertThat(order.isPending()).isTrue();
    }

    @Test
    @DisplayName("Should not be able to cancel when status is PAID")
    void shouldNotBeAbleToCancel_WhenStatusIsPaid() {
        OrderDomain order = createOrder(createOrderItems(), OrderDomain.OrderStatus.PAID);

        assertThat(order.isPaid()).isTrue();
    }

    @Test
    @DisplayName("Should not be able to cancel when status is CANCELLED")
    void shouldNotBeAbleToCancel_WhenStatusIsCancelled() {
        OrderDomain order = createOrder(createOrderItems(), OrderDomain.OrderStatus.CANCELLED);

        assertThat(order.isCancelled()).isTrue();
    }

    @Test
    @DisplayName("Should be able to pay when status is PENDING")
    void shouldBeAbleToPay_WhenStatusIsPending() {
        OrderDomain order = createOrder(createOrderItems(), OrderDomain.OrderStatus.PENDING);

        assertThat(order.isPending()).isTrue();
        assertThat(order.isPaid()).isFalse();
    }

    @Test
    @DisplayName("Should not be able to pay when status is PAID")
    void shouldNotBeAbleToPay_WhenStatusIsPaid() {
        OrderDomain order = createOrder(createOrderItems(), OrderDomain.OrderStatus.PAID);

        assertThat(order.isPaid()).isTrue();
        assertThat(order.isPending()).isFalse();
    }

    @Test
    @DisplayName("Should not be able to pay when status is CANCELLED")
    void shouldNotBeAbleToPay_WhenStatusIsCancelled() {
        OrderDomain order = createOrder(createOrderItems(), OrderDomain.OrderStatus.CANCELLED);

        assertThat(order.isCancelled()).isTrue();
        assertThat(order.isPaid()).isFalse();
    }

    @Test
    @DisplayName("Should mark order as paid successfully when order can be paid")
    void shouldMarkOrderAsPaid_WhenOrderCanBePaid() {
        OrderDomain order = createOrder(createOrderItems(), OrderDomain.OrderStatus.PENDING);

        OrderDomain paidOrder = order.markAsPaid();

        assertThat(paidOrder.getStatus()).isEqualTo(OrderDomain.OrderStatus.PAID);
    }

    @Test
    @DisplayName("Should throw exception when trying to mark already paid order as paid")
    void shouldThrowException_WhenTryingToMarkAlreadyPaidOrderAsPaid() {
        OrderDomain order = createOrder(createOrderItems(), OrderDomain.OrderStatus.PAID);

        assertThatThrownBy(order::markAsPaid)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only pending orders can be paid");
    }

    @Test
    @DisplayName("Should throw exception when trying to mark cancelled order as paid")
    void shouldThrowException_WhenTryingToMarkCancelledOrderAsPaid() {
        OrderDomain order = createOrder(createOrderItems(), OrderDomain.OrderStatus.CANCELLED);

        assertThatThrownBy(order::markAsPaid)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only pending orders can be paid");
    }

    @Test
    @DisplayName("Should cancel order successfully when order can be cancelled")
    void shouldCancelOrder_WhenOrderCanBeCancelled() {
        OrderDomain order = createOrder(createOrderItems(), OrderDomain.OrderStatus.PENDING);

        OrderDomain cancelledOrder = order.cancel();

        assertThat(cancelledOrder.getStatus()).isEqualTo(OrderDomain.OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should throw exception when trying to cancel already paid order")
    void shouldThrowException_WhenTryingToCancelAlreadyPaidOrder() {
        OrderDomain order = createOrder(createOrderItems(), OrderDomain.OrderStatus.PAID);

        assertThatThrownBy(order::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Paid orders cannot be cancelled");
    }

    @Test
    @DisplayName("Should cancel already cancelled order successfully")
    void shouldCancelAlreadyCancelledOrder_Successfully() {
        OrderDomain order = createOrder(createOrderItems(), OrderDomain.OrderStatus.CANCELLED);

        OrderDomain cancelledOrder = order.cancel();

        assertThat(cancelledOrder.getStatus()).isEqualTo(OrderDomain.OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should check if order is pending")
    void shouldCheckIfOrderIsPending() {
        OrderDomain pendingOrder = createOrder(createOrderItems(), OrderDomain.OrderStatus.PENDING);
        OrderDomain paidOrder = createOrder(createOrderItems(), OrderDomain.OrderStatus.PAID);

        assertThat(pendingOrder.isPending()).isTrue();
        assertThat(paidOrder.isPending()).isFalse();
    }

    @Test
    @DisplayName("Should check if order is paid")
    void shouldCheckIfOrderIsPaid() {
        OrderDomain pendingOrder = createOrder(createOrderItems(), OrderDomain.OrderStatus.PENDING);
        OrderDomain paidOrder = createOrder(createOrderItems(), OrderDomain.OrderStatus.PAID);

        assertThat(pendingOrder.isPaid()).isFalse();
        assertThat(paidOrder.isPaid()).isTrue();
    }

    @Test
    @DisplayName("Should check if order is cancelled")
    void shouldCheckIfOrderIsCancelled() {
        OrderDomain pendingOrder = createOrder(createOrderItems(), OrderDomain.OrderStatus.PENDING);
        OrderDomain cancelledOrder = createOrder(createOrderItems(), OrderDomain.OrderStatus.CANCELLED);

        assertThat(pendingOrder.isCancelled()).isFalse();
        assertThat(cancelledOrder.isCancelled()).isTrue();
    }

    private List<OrderItemDomain> createOrderItems() {
        return Arrays.asList(
                new OrderItemDomain(ProductId.generate(), "Product 1", new Money(new BigDecimal("99.99")), 1),
                new OrderItemDomain(ProductId.generate(), "Product 2", new Money(new BigDecimal("49.99")), 2));
    }

    private OrderDomain createOrder(List<OrderItemDomain> items, OrderDomain.OrderStatus status) {
        return new OrderDomain(
                OrderId.generate(),
                UserId.generate(),
                items,
                new Money(new BigDecimal("199.98")),
                status,
                LocalDateTime.now());
    }
}
