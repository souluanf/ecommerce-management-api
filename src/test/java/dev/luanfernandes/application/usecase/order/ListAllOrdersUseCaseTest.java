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
@DisplayName("Tests for ListAllOrdersUseCase")
class ListAllOrdersUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ListAllOrdersUseCase listAllOrdersUseCase;

    @Test
    @DisplayName("Should return all orders when orders exist")
    void shouldReturnAllOrders_WhenOrdersExist() {
        List<OrderDomain> expectedOrders = Arrays.asList(
                createOrder(OrderDomain.OrderStatus.PENDING),
                createOrder(OrderDomain.OrderStatus.PAID),
                createOrder(OrderDomain.OrderStatus.CANCELLED));

        when(orderRepository.findAll()).thenReturn(expectedOrders);

        List<OrderDomain> result = listAllOrdersUseCase.execute();

        assertThat(result).hasSize(3).containsExactlyElementsOf(expectedOrders);
        assertThat(result.get(0).getStatus()).isEqualTo(OrderDomain.OrderStatus.PENDING);
        assertThat(result.get(1).getStatus()).isEqualTo(OrderDomain.OrderStatus.PAID);
        assertThat(result.get(2).getStatus()).isEqualTo(OrderDomain.OrderStatus.CANCELLED);

        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no orders exist")
    void shouldReturnEmptyList_WhenNoOrdersExist() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        List<OrderDomain> result = listAllOrdersUseCase.execute();

        assertThat(result).isEmpty();

        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("Should return single order when only one order exists")
    void shouldReturnSingleOrder_WhenOnlyOneOrderExists() {
        OrderDomain singleOrder = createOrder(OrderDomain.OrderStatus.PENDING);
        List<OrderDomain> expectedOrders = Collections.singletonList(singleOrder);

        when(orderRepository.findAll()).thenReturn(expectedOrders);

        List<OrderDomain> result = listAllOrdersUseCase.execute();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(singleOrder);
        assertThat(result.getFirst().getStatus()).isEqualTo(OrderDomain.OrderStatus.PENDING);

        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("Should return paginated orders when using pagination")
    void shouldReturnPaginatedOrders_WhenUsingPagination() {
        PageRequest pageRequest = PageRequest.of(0, 10, "createdAt");
        List<OrderDomain> orders =
                Arrays.asList(createOrder(OrderDomain.OrderStatus.PENDING), createOrder(OrderDomain.OrderStatus.PAID));
        PageResponse<OrderDomain> expectedResponse = new PageResponse<>(0, 10, 2L, false, orders);

        when(orderRepository.findAllPaginated(pageRequest)).thenReturn(expectedResponse);

        PageResponse<OrderDomain> result = listAllOrdersUseCase.execute(pageRequest);

        assertThat(result.content()).hasSize(2);
        assertThat(result.pageNumber()).isZero();
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.elements()).isEqualTo(2L);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.hasNext()).isFalse();

        verify(orderRepository).findAllPaginated(pageRequest);
    }

    @Test
    @DisplayName("Should return empty page when no orders exist with pagination")
    void shouldReturnEmptyPage_WhenNoOrdersExistWithPagination() {
        PageRequest pageRequest = PageRequest.of(0, 10, "createdAt");
        PageResponse<OrderDomain> expectedResponse = new PageResponse<>(0, 10, 0L, false, Collections.emptyList());

        when(orderRepository.findAllPaginated(pageRequest)).thenReturn(expectedResponse);

        PageResponse<OrderDomain> result = listAllOrdersUseCase.execute(pageRequest);

        assertThat(result.content()).isEmpty();
        assertThat(result.pageNumber()).isZero();
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.elements()).isZero();
        assertThat(result.getTotalPages()).isZero();
        assertThat(result.hasNext()).isFalse();

        verify(orderRepository).findAllPaginated(pageRequest);
    }

    @Test
    @DisplayName("Should handle pagination with multiple pages")
    void shouldHandlePagination_WithMultiplePages() {
        PageRequest pageRequest = PageRequest.of(1, 5, "createdAt");
        List<OrderDomain> orders = Arrays.asList(
                createOrder(OrderDomain.OrderStatus.PENDING),
                createOrder(OrderDomain.OrderStatus.PAID),
                createOrder(OrderDomain.OrderStatus.CANCELLED));
        PageResponse<OrderDomain> expectedResponse = new PageResponse<>(1, 5, 15L, true, orders);

        when(orderRepository.findAllPaginated(pageRequest)).thenReturn(expectedResponse);

        PageResponse<OrderDomain> result = listAllOrdersUseCase.execute(pageRequest);

        assertThat(result.content()).hasSize(3);
        assertThat(result.pageNumber()).isEqualTo(1);
        assertThat(result.pageSize()).isEqualTo(5);
        assertThat(result.elements()).isEqualTo(15L);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.hasNext()).isTrue();

        verify(orderRepository).findAllPaginated(pageRequest);
    }

    @Test
    @DisplayName("Should handle orders with different statuses and amounts")
    void shouldHandleOrders_WithDifferentStatusesAndAmounts() {
        List<OrderDomain> expectedOrders = Arrays.asList(
                createOrderWithAmount(OrderDomain.OrderStatus.PENDING, new BigDecimal("99.99")),
                createOrderWithAmount(OrderDomain.OrderStatus.PAID, new BigDecimal("249.50")),
                createOrderWithAmount(OrderDomain.OrderStatus.CANCELLED, new BigDecimal("149.99")));

        when(orderRepository.findAll()).thenReturn(expectedOrders);

        List<OrderDomain> result = listAllOrdersUseCase.execute();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getTotalAmount()).isEqualTo(new Money(new BigDecimal("99.99")));
        assertThat(result.get(1).getTotalAmount()).isEqualTo(new Money(new BigDecimal("249.50")));
        assertThat(result.get(2).getTotalAmount()).isEqualTo(new Money(new BigDecimal("149.99")));

        verify(orderRepository).findAll();
    }

    private OrderDomain createOrder(OrderDomain.OrderStatus status) {
        return createOrderWithAmount(status, new BigDecimal("99.99"));
    }

    private OrderDomain createOrderWithAmount(OrderDomain.OrderStatus status, BigDecimal amount) {
        OrderItemDomain orderItem = new OrderItemDomain(ProductId.generate(), "Test Product", new Money(amount), 1);
        return new OrderDomain(
                OrderId.generate(),
                UserId.generate(),
                List.of(orderItem),
                new Money(amount),
                status,
                LocalDateTime.now());
    }
}
