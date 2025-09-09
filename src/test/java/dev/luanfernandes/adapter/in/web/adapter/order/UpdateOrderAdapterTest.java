package dev.luanfernandes.adapter.in.web.adapter.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.luanfernandes.application.usecase.order.UpdateOrderUseCase;
import dev.luanfernandes.domain.dto.OrderItemRequest;
import dev.luanfernandes.domain.dto.UpdateOrderRequest;
import dev.luanfernandes.domain.dto.command.UpdateOrderCommand;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.entity.ProductDomain;
import dev.luanfernandes.domain.exception.InvalidOrderStatusException;
import dev.luanfernandes.domain.exception.ProductNotFoundException;
import dev.luanfernandes.domain.valueobject.Money;
import dev.luanfernandes.domain.valueobject.OrderId;
import dev.luanfernandes.domain.valueobject.ProductId;
import dev.luanfernandes.domain.valueobject.UserId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        classes = {UpdateOrderAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class})
class UpdateOrderAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UpdateOrderUseCase updateOrderUseCase;

    @Test
    void shouldUpdateOrderSuccessfully() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String productId = UUID.randomUUID().toString();

        List<OrderItemRequest> items = List.of(new OrderItemRequest(productId, 3));

        UpdateOrderRequest request = new UpdateOrderRequest(items);

        ProductDomain product = new ProductDomain(
                ProductId.of(UUID.fromString(productId)),
                "Updated Product",
                "Description",
                Money.of(new BigDecimal("150.00")),
                "Electronics",
                10,
                LocalDateTime.now());

        OrderItemDomain orderItem = new OrderItemDomain(product.getId(), product.getName(), product.getPrice(), 3);

        OrderDomain updatedOrder = new OrderDomain(
                OrderId.of(orderId),
                UserId.of(userId),
                List.of(orderItem),
                Money.of(new BigDecimal("450.00")),
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());

        when(updateOrderUseCase.update(any(UpdateOrderCommand.class))).thenReturn(Optional.of(updatedOrder));

        mockMvc.perform(put("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.totalAmount").value(450.00))
                .andExpect(jsonPath("$.items[0].quantity").value(3));
    }

    @Test
    void shouldReturnNotFound_WhenOrderDoesNotExist() throws Exception {
        UUID orderId = UUID.randomUUID();
        String productId = UUID.randomUUID().toString();

        List<OrderItemRequest> items = List.of(new OrderItemRequest(productId, 2));

        UpdateOrderRequest request = new UpdateOrderRequest(items);

        when(updateOrderUseCase.update(any(UpdateOrderCommand.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_WhenOrderAlreadyPaid() throws Exception {
        UUID orderId = UUID.randomUUID();
        String productId = UUID.randomUUID().toString();

        List<OrderItemRequest> items = List.of(new OrderItemRequest(productId, 2));

        UpdateOrderRequest request = new UpdateOrderRequest(items);

        when(updateOrderUseCase.update(any(UpdateOrderCommand.class)))
                .thenThrow(new InvalidOrderStatusException("Cannot update paid order"));

        mockMvc.perform(put("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequest_WhenProductNotFound() throws Exception {
        UUID orderId = UUID.randomUUID();
        String productId = UUID.randomUUID().toString();

        List<OrderItemRequest> items = List.of(new OrderItemRequest(productId, 2));

        UpdateOrderRequest request = new UpdateOrderRequest(items);

        when(updateOrderUseCase.update(any(UpdateOrderCommand.class)))
                .thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(put("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_WhenEmptyItems() throws Exception {
        UUID orderId = UUID.randomUUID();

        UpdateOrderRequest request = new UpdateOrderRequest(List.of());

        mockMvc.perform(put("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateOrderWithMultipleItems() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String productId1 = UUID.randomUUID().toString();
        String productId2 = UUID.randomUUID().toString();

        List<OrderItemRequest> items =
                List.of(new OrderItemRequest(productId1, 1), new OrderItemRequest(productId2, 2));

        UpdateOrderRequest request = new UpdateOrderRequest(items);

        ProductDomain product1 = new ProductDomain(
                ProductId.of(UUID.fromString(productId1)),
                "Product 1",
                "Description 1",
                Money.of(new BigDecimal("100.00")),
                "Electronics",
                10,
                LocalDateTime.now());

        ProductDomain product2 = new ProductDomain(
                ProductId.of(UUID.fromString(productId2)),
                "Product 2",
                "Description 2",
                Money.of(new BigDecimal("50.00")),
                "Electronics",
                20,
                LocalDateTime.now());

        List<OrderItemDomain> orderItems = List.of(
                new OrderItemDomain(product1.getId(), product1.getName(), product1.getPrice(), 1),
                new OrderItemDomain(product2.getId(), product2.getName(), product2.getPrice(), 2));

        OrderDomain updatedOrder = new OrderDomain(
                OrderId.of(orderId),
                UserId.of(userId),
                orderItems,
                Money.of(new BigDecimal("200.00")),
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());

        when(updateOrderUseCase.update(any(UpdateOrderCommand.class))).thenReturn(Optional.of(updatedOrder));

        mockMvc.perform(put("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.totalAmount").value(200.00));
    }
}
