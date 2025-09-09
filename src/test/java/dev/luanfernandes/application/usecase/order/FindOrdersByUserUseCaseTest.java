package dev.luanfernandes.application.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.luanfernandes.domain.dto.PageRequest;
import dev.luanfernandes.domain.dto.PageResponse;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.port.out.order.OrderRepository;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for FindOrdersByUserUseCase")
class FindOrdersByUserUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private FindOrdersByUserUseCase findOrdersByUserUseCase;

    @Test
    @DisplayName("Should return orders list when user has orders")
    void shouldReturnOrdersList_WhenUserHasOrders() {
        UserId userId = UserId.generate();
        List<OrderDomain> expectedOrders = Arrays.asList(
                createOrder(userId, OrderDomain.OrderStatus.PENDING),
                createOrder(userId, OrderDomain.OrderStatus.PAID),
                createOrder(userId, OrderDomain.OrderStatus.CANCELLED));

        when(orderRepository.findByUserId(userId)).thenReturn(expectedOrders);

        List<OrderDomain> result = findOrdersByUserUseCase.execute(userId);

        assertThat(result).hasSize(3).containsExactlyElementsOf(expectedOrders).allSatisfy(order -> assertThat(
                        order.getUserId())
                .isEqualTo(userId));

        verify(orderRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Should return empty list when user has no orders")
    void shouldReturnEmptyList_WhenUserHasNoOrders() {
        UserId userId = UserId.generate();

        when(orderRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<OrderDomain> result = findOrdersByUserUseCase.execute(userId);

        assertThat(result).isEmpty();

        verify(orderRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Should return single order when user has one order")
    void shouldReturnSingleOrder_WhenUserHasOneOrder() {
        UserId userId = UserId.generate();
        OrderDomain singleOrder = createOrder(userId, OrderDomain.OrderStatus.PENDING);
        List<OrderDomain> expectedOrders = Collections.singletonList(singleOrder);

        when(orderRepository.findByUserId(userId)).thenReturn(expectedOrders);

        List<OrderDomain> result = findOrdersByUserUseCase.execute(userId);

        assertThat(result).hasSize(1).first().isEqualTo(singleOrder).satisfies(order -> assertThat(order.getUserId())
                .isEqualTo(userId));

        verify(orderRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Should return paginated orders when using pagination")
    void shouldReturnPaginatedOrders_WhenUsingPagination() {
        UserId userId = UserId.generate();
        PageRequest pageRequest = PageRequest.of(0, 10, "createdAt");
        List<OrderDomain> orders = Arrays.asList(
                createOrder(userId, OrderDomain.OrderStatus.PENDING),
                createOrder(userId, OrderDomain.OrderStatus.PAID));
        PageResponse<OrderDomain> expectedResponse = new PageResponse<>(0, 10, 2L, false, orders);

        when(orderRepository.findByUserIdPaginated(userId, pageRequest)).thenReturn(expectedResponse);

        PageResponse<OrderDomain> result = findOrdersByUserUseCase.execute(userId, pageRequest);

        assertThat(result.content()).hasSize(2).allSatisfy(order -> assertThat(order.getUserId())
                .isEqualTo(userId));

        assertThat(result).satisfies(response -> {
            assertThat(response.pageNumber()).isZero();
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.elements()).isEqualTo(2L);
            assertThat(response.getTotalPages()).isEqualTo(1);
        });

        verify(orderRepository).findByUserIdPaginated(userId, pageRequest);
    }

    @Test
    @DisplayName("Should return empty page when user has no orders with pagination")
    void shouldReturnEmptyPage_WhenUserHasNoOrdersWithPagination() {
        UserId userId = UserId.generate();
        PageRequest pageRequest = PageRequest.of(0, 10, "createdAt");
        PageResponse<OrderDomain> expectedResponse = new PageResponse<>(0, 10, 0L, false, Collections.emptyList());

        when(orderRepository.findByUserIdPaginated(userId, pageRequest)).thenReturn(expectedResponse);

        PageResponse<OrderDomain> result = findOrdersByUserUseCase.execute(userId, pageRequest);

        assertThat(result.content()).isEmpty();

        assertThat(result).satisfies(response -> {
            assertThat(response.pageNumber()).isZero();
            assertThat(response.pageSize()).isEqualTo(10);
            assertThat(response.elements()).isZero();
            assertThat(response.getTotalPages()).isZero();
        });

        verify(orderRepository).findByUserIdPaginated(userId, pageRequest);
    }

    @Test
    @DisplayName("Should handle different order statuses correctly")
    void shouldHandleDifferentOrderStatuses() {
        UserId userId = UserId.generate();
        List<OrderDomain> expectedOrders = Arrays.asList(
                createOrder(userId, OrderDomain.OrderStatus.PENDING),
                createOrder(userId, OrderDomain.OrderStatus.PAID),
                createOrder(userId, OrderDomain.OrderStatus.CANCELLED));

        when(orderRepository.findByUserId(userId)).thenReturn(expectedOrders);

        List<OrderDomain> result = findOrdersByUserUseCase.execute(userId);

        assertThat(result).hasSize(3).satisfies(orders -> {
            assertThat(orders.get(0).getStatus()).isEqualTo(OrderDomain.OrderStatus.PENDING);
            assertThat(orders.get(1).getStatus()).isEqualTo(OrderDomain.OrderStatus.PAID);
            assertThat(orders.get(2).getStatus()).isEqualTo(OrderDomain.OrderStatus.CANCELLED);
        });

        verify(orderRepository).findByUserId(userId);
    }

    private OrderDomain createOrder(UserId userId, OrderDomain.OrderStatus status) {
        OrderItemDomain orderItem =
                new OrderItemDomain(ProductId.generate(), "Test Product", new Money(new BigDecimal("99.99")), 1);
        return new OrderDomain(
                OrderId.generate(),
                userId,
                List.of(orderItem),
                new Money(new BigDecimal("99.99")),
                status,
                LocalDateTime.now());
    }
}
