package dev.luanfernandes.application.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.lang.reflect.Method;
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
@DisplayName("Tests for CreateOrderUseCase")
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    @Test
    @DisplayName("Should create order successfully with sufficient stock")
    void shouldCreateOrder_WhenSufficientStock() {
        UserId userId = UserId.generate();
        ProductId productId = ProductId.generate();
        ProductDomain product = createProduct(productId, 100);
        OrderItemCommand orderItem = new OrderItemCommand(productId, 5);
        CreateOrderCommand command = new CreateOrderCommand(userId, List.of(orderItem));

        OrderDomain expectedOrder = createOrder(userId, product);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(ProductDomain.class))).thenReturn(product);
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(expectedOrder);

        OrderDomain result = createOrderUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getStatus()).isEqualTo(OrderDomain.OrderStatus.PENDING);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalAmount()).isEqualTo(new Money(new BigDecimal("499.95")));

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(ProductDomain.class));
        verify(orderRepository).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found")
    void shouldThrowProductNotFoundException_WhenProductNotFound() {
        UserId userId = UserId.generate();
        ProductId productId = ProductId.generate();
        OrderItemCommand orderItem = new OrderItemCommand(productId, 5);
        CreateOrderCommand command = new CreateOrderCommand(userId, List.of(orderItem));

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createOrderUseCase.execute(command)).isInstanceOf(ProductNotFoundException.class);

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should create cancelled order when insufficient stock")
    void shouldCreateCancelledOrder_WhenInsufficientStock() {
        UserId userId = UserId.generate();
        ProductId productId = ProductId.generate();
        ProductDomain product = createProduct(productId, 3);
        OrderItemCommand orderItem = new OrderItemCommand(productId, 10);
        CreateOrderCommand command = new CreateOrderCommand(userId, List.of(orderItem));

        OrderDomain cancelledOrder = createCancelledOrder(userId, product, 10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(cancelledOrder);

        OrderDomain result = createOrderUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderDomain.OrderStatus.CANCELLED);
        assertThat(result.getItems()).hasSize(1);

        verify(productRepository).findById(productId);
        verify(orderRepository).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("Should create order with multiple items")
    void shouldCreateOrder_WithMultipleItems() {
        UserId userId = UserId.generate();
        ProductId productId1 = ProductId.generate();
        ProductId productId2 = ProductId.generate();

        ProductDomain product1 = createProduct(productId1, 50);
        ProductDomain product2 = createProduct(productId2, 30, new BigDecimal("29.99"));

        OrderItemCommand item1 = new OrderItemCommand(productId1, 2);
        OrderItemCommand item2 = new OrderItemCommand(productId2, 3);
        CreateOrderCommand command = new CreateOrderCommand(userId, Arrays.asList(item1, item2));

        OrderDomain expectedOrder = createMultiItemOrder(userId, product1, product2);

        when(productRepository.findById(productId1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(productId2)).thenReturn(Optional.of(product2));
        when(productRepository.save(any(ProductDomain.class))).thenReturn(product1, product2);
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(expectedOrder);

        OrderDomain result = createOrderUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getStatus()).isEqualTo(OrderDomain.OrderStatus.PENDING);

        verify(productRepository).findById(productId1);
        verify(productRepository).findById(productId2);
        verify(orderRepository).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("Should rollback stock when partial order fails")
    void shouldRollbackStock_WhenPartialOrderFails() {
        UserId userId = UserId.generate();
        ProductId productId1 = ProductId.generate();
        ProductId productId2 = ProductId.generate();

        ProductDomain product1 = createProduct(productId1, 10);
        ProductDomain product2 = createProduct(productId2, 2);

        OrderItemCommand item1 = new OrderItemCommand(productId1, 3);
        OrderItemCommand item2 = new OrderItemCommand(productId2, 5);
        CreateOrderCommand command = new CreateOrderCommand(userId, Arrays.asList(item1, item2));

        OrderDomain cancelledOrder = createCancelledOrder(userId, product1, 3);

        when(productRepository.findById(productId1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(productId2)).thenReturn(Optional.of(product2));
        when(productRepository.save(any(ProductDomain.class))).thenReturn(product1, product2);
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(cancelledOrder);

        OrderDomain result = createOrderUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderDomain.OrderStatus.CANCELLED);

        verify(productRepository).findById(productId1);
        verify(productRepository).findById(productId2);
        verify(orderRepository).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for zero quantity order item")
    void shouldThrowIllegalArgumentException_WhenZeroQuantity() {
        UserId userId = UserId.generate();
        ProductId productId = ProductId.generate();
        ProductDomain product = createProduct(productId, 100);
        OrderItemCommand orderItem = new OrderItemCommand(productId, 0);
        CreateOrderCommand command = new CreateOrderCommand(userId, List.of(orderItem));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> createOrderUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be positive");

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should create cancelled order when stock reservation fails with exception")
    void shouldCreateCancelledOrder_WhenStockReservationFails() {
        UserId userId = UserId.generate();
        ProductId productId = ProductId.generate();
        ProductDomain product = createProduct(productId, 10);
        OrderItemCommand orderItem = new OrderItemCommand(productId, 5);
        CreateOrderCommand command = new CreateOrderCommand(userId, List.of(orderItem));

        OrderDomain cancelledOrder = createCancelledOrder(userId, product, 5);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(ProductDomain.class)))
                .thenThrow(new IllegalStateException("Stock reservation failed"));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(cancelledOrder);

        OrderDomain result = createOrderUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderDomain.OrderStatus.CANCELLED);

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(ProductDomain.class));
        verify(orderRepository).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("Should handle rollback exception gracefully")
    void shouldHandleRollbackException_Gracefully() {
        UserId userId = UserId.generate();
        ProductId productId1 = ProductId.generate();
        ProductId productId2 = ProductId.generate();

        ProductDomain product1 = createProduct(productId1, 10);
        ProductDomain product2 = createProduct(productId2, 2);

        OrderItemCommand item1 = new OrderItemCommand(productId1, 3);
        OrderItemCommand item2 = new OrderItemCommand(productId2, 5);
        CreateOrderCommand command = new CreateOrderCommand(userId, Arrays.asList(item1, item2));

        OrderDomain cancelledOrder = createCancelledOrder(userId, product1, 3);

        when(productRepository.findById(productId1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(productId2)).thenReturn(Optional.of(product2));
        when(productRepository.save(any(ProductDomain.class)))
                .thenReturn(product1.reduceStock(3))
                .thenThrow(new RuntimeException("Database error during rollback"));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(cancelledOrder);

        OrderDomain result = createOrderUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderDomain.OrderStatus.CANCELLED);

        verify(productRepository).findById(productId1);
        verify(productRepository).findById(productId2);
        verify(orderRepository).save(any(OrderDomain.class));
    }

    private ProductDomain createProduct(ProductId productId, int stock) {
        return createProduct(productId, stock, new BigDecimal("99.99"));
    }

    private ProductDomain createProduct(ProductId productId, int stock, BigDecimal price) {
        return new ProductDomain(
                productId,
                "Test Product",
                "Test Description",
                new Money(price),
                "ELECTRONICS",
                stock,
                LocalDateTime.now());
    }

    private OrderDomain createOrder(UserId userId, ProductDomain product) {
        OrderItemDomain orderItem = new OrderItemDomain(product.getId(), product.getName(), product.getPrice(), 5);
        return new OrderDomain(
                OrderId.generate(),
                userId,
                List.of(orderItem),
                new Money(product.getPrice().value().multiply(BigDecimal.valueOf(Math.max(5, 0)))),
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());
    }

    private OrderDomain createCancelledOrder(UserId userId, ProductDomain product, int quantity) {
        OrderItemDomain orderItem =
                new OrderItemDomain(product.getId(), product.getName(), product.getPrice(), quantity);
        return new OrderDomain(
                OrderId.generate(),
                userId,
                List.of(orderItem),
                new Money(product.getPrice().value().multiply(BigDecimal.valueOf(quantity))),
                OrderDomain.OrderStatus.CANCELLED,
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Should skip rollback when reserved quantity is zero using reflection")
    void shouldSkipRollback_WhenReservedQuantityIsZero() throws Exception {
        UserId userId = UserId.generate();
        ProductId productId1 = ProductId.generate();
        ProductId productId2 = ProductId.generate();

        ProductDomain reservedProduct1 = createProduct(productId1, 5);
        ProductDomain reservedProduct2 = createProduct(productId2, 3);
        List<ProductDomain> reservedProducts = Arrays.asList(reservedProduct1, reservedProduct2);

        OrderItemCommand item2 = new OrderItemCommand(productId2, 2);
        CreateOrderCommand command = new CreateOrderCommand(userId, List.of(item2));
        Method rollbackMethod = CreateOrderUseCase.class.getDeclaredMethod(
                "rollbackReservedStock", List.class, CreateOrderCommand.class);
        rollbackMethod.setAccessible(true);
        rollbackMethod.invoke(createOrderUseCase, reservedProducts, command);
        verify(productRepository, times(1)).save(any(ProductDomain.class));
    }

    private OrderDomain createMultiItemOrder(UserId userId, ProductDomain product1, ProductDomain product2) {
        OrderItemDomain orderItem1 = new OrderItemDomain(product1.getId(), product1.getName(), product1.getPrice(), 2);
        OrderItemDomain orderItem2 = new OrderItemDomain(product2.getId(), product2.getName(), product2.getPrice(), 3);
        Money totalAmount = new Money(product1.getPrice().value().multiply(BigDecimal.valueOf(2)))
                .add(new Money(product2.getPrice().value().multiply(BigDecimal.valueOf(3))));
        return new OrderDomain(
                OrderId.generate(),
                userId,
                List.of(orderItem1, orderItem2),
                totalAmount,
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());
    }
}
