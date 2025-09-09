package dev.luanfernandes.adapter.in.web.adapter.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.luanfernandes.application.usecase.order.FindOrderByIdUseCase;
import dev.luanfernandes.domain.entity.OrderDomain;
import dev.luanfernandes.domain.entity.OrderItemDomain;
import dev.luanfernandes.domain.entity.ProductDomain;
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
        classes = {GetOrderAdapter.class, dev.luanfernandes.infrastructure.config.web.ExceptionHandlerAdvice.class})
class GetOrderAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FindOrderByIdUseCase findOrderByIdUseCase;

    @Test
    void shouldGetOrderSuccessfully() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ProductDomain product = new ProductDomain(
                ProductId.of(productId),
                "Test Product",
                "Description",
                Money.of(new BigDecimal("100.00")),
                "Electronics",
                10,
                LocalDateTime.now());

        OrderItemDomain orderItem = new OrderItemDomain(product.getId(), product.getName(), product.getPrice(), 2);

        OrderDomain order = new OrderDomain(
                OrderId.of(orderId),
                UserId.of(userId),
                List.of(orderItem),
                Money.of(new BigDecimal("200.00")),
                OrderDomain.OrderStatus.PENDING,
                LocalDateTime.now());

        when(findOrderByIdUseCase.execute(any(OrderId.class))).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/v1/orders/{id}", orderId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(200.00))
                .andExpect(jsonPath("$.items[0].productId").value(productId.toString()))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void shouldReturnNotFound_WhenOrderDoesNotExist() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(findOrderByIdUseCase.execute(any(OrderId.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/orders/{id}", orderId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_WhenInvalidUUID() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(get("/api/v1/orders/{id}", invalidId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetOrderWithMultipleItems() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        ProductDomain product1 = new ProductDomain(
                ProductId.of(productId1),
                "Product 1",
                "Description 1",
                Money.of(new BigDecimal("50.00")),
                "Electronics",
                10,
                LocalDateTime.now());

        ProductDomain product2 = new ProductDomain(
                ProductId.of(productId2),
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
                OrderId.of(orderId),
                UserId.of(userId),
                orderItems,
                Money.of(new BigDecimal("190.00")),
                OrderDomain.OrderStatus.PAID,
                LocalDateTime.now());

        when(findOrderByIdUseCase.execute(any(OrderId.class))).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/v1/orders/{id}", orderId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.totalAmount").value(190.00));
    }
}
