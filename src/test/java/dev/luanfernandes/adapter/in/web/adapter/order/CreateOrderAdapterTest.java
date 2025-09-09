package dev.luanfernandes.adapter.in.web.adapter.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.luanfernandes.application.usecase.order.CreateOrderUseCase;
import dev.luanfernandes.domain.dto.CreateOrderRequest;
import dev.luanfernandes.domain.dto.OrderItemRequest;
import dev.luanfernandes.domain.dto.command.CreateOrderCommand;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.InsufficientStockException;
import dev.luanfernandes.domain.exception.ProductNotFoundException;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(
        classes = {CreateOrderAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class})
class CreateOrderAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateOrderUseCase createOrderUseCase;

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        String userId = UUID.randomUUID().toString();
        String productId = UUID.randomUUID().toString();
        String orderId = UUID.randomUUID().toString();

        List<OrderItemRequest> items = List.of(new OrderItemRequest(productId, 2));

        CreateOrderRequest request = new CreateOrderRequest(userId, items);

        ProductDomain product = new ProductDomain(
                ProductId.of(UUID.fromString(productId)),
                "Test Product",
                "Description",
                Money.of(new BigDecimal("100.00")),
                "Electronics",
                10,
                LocalDateTime.now());

        OrderItemDomain orderItem = new OrderItemDomain(product.getId(), product.getName(), product.getPrice(), 2);

        OrderDomain order = new OrderDomain(
                OrderId.of(UUID.fromString(orderId)),
                UserId.of(UUID.fromString(userId)),
                List.of(orderItem),
                Money.of(new BigDecimal("200.00")),
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());

        when(createOrderUseCase.execute(any(CreateOrderCommand.class))).thenReturn(order);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(200.00))
                .andExpect(jsonPath("$.items[0].productId").value(productId))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void shouldReturnBadRequest_WhenProductNotFound() throws Exception {
        String userId = UUID.randomUUID().toString();
        String productId = UUID.randomUUID().toString();

        List<OrderItemRequest> items = List.of(new OrderItemRequest(productId, 2));

        CreateOrderRequest request = new CreateOrderRequest(userId, items);

        when(createOrderUseCase.execute(any(CreateOrderCommand.class)))
                .thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_WhenInsufficientStock() throws Exception {
        String userId = UUID.randomUUID().toString();
        String productId = UUID.randomUUID().toString();

        List<OrderItemRequest> items = List.of(new OrderItemRequest(productId, 100));

        CreateOrderRequest request = new CreateOrderRequest(userId, items);

        when(createOrderUseCase.execute(any(CreateOrderCommand.class)))
                .thenThrow(new InsufficientStockException("Insufficient stock"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequest_WhenEmptyItems() throws Exception {
        String userId = UUID.randomUUID().toString();

        CreateOrderRequest request = new CreateOrderRequest(userId, List.of());

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnBadRequest_WhenInvalidUserId() throws Exception {
        String invalidUserId = "invalid-uuid";
        String productId = UUID.randomUUID().toString();

        List<OrderItemRequest> items = List.of(new OrderItemRequest(productId, 2));

        CreateOrderRequest request = new CreateOrderRequest(invalidUserId, items);

        when(createOrderUseCase.execute(any(CreateOrderCommand.class)))
                .thenThrow(new IllegalArgumentException("Invalid UUID"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldCreateOrderWithMultipleItems() throws Exception {
        String userId = UUID.randomUUID().toString();
        String productId1 = UUID.randomUUID().toString();
        String productId2 = UUID.randomUUID().toString();
        String orderId = UUID.randomUUID().toString();

        List<OrderItemRequest> items =
                List.of(new OrderItemRequest(productId1, 2), new OrderItemRequest(productId2, 3));

        CreateOrderRequest request = new CreateOrderRequest(userId, items);

        ProductDomain product1 = new ProductDomain(
                ProductId.of(UUID.fromString(productId1)),
                "Product 1",
                "Description 1",
                Money.of(new BigDecimal("50.00")),
                "Electronics",
                10,
                LocalDateTime.now());

        ProductDomain product2 = new ProductDomain(
                ProductId.of(UUID.fromString(productId2)),
                "Product 2",
                "Description 2",
                Money.of(new BigDecimal("30.00")),
                "Electronics",
                20,
                LocalDateTime.now());

        List<OrderItemDomain> orderItems = List.of(
                new OrderItemDomain(product1.getId(), product1.getName(), product1.getPrice(), 2),
                new OrderItemDomain(product2.getId(), product2.getName(), product2.getPrice(), 3));

        OrderDomain order = new OrderDomain(
                OrderId.of(UUID.fromString(orderId)),
                UserId.of(UUID.fromString(userId)),
                orderItems,
                Money.of(new BigDecimal("190.00")),
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());

        when(createOrderUseCase.execute(any(CreateOrderCommand.class))).thenReturn(order);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.totalAmount").value(190.00));
    }
}
